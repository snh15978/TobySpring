package springbook.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.dao.DaoFactory;
import springbook.dao.UserDao;
import springbook.domain.Level;
import springbook.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;
import static springbook.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.service.UserService.MIN_RECOMMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoFactory.class)
public class UserServiceTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    UserService userService;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    MailSender mailSender;

    List<User> users;

    @Before
    public void setUp(){
        users = Arrays.asList(
                new User("a","A","p1", Level.BASIC,MIN_LOGCOUNT_FOR_SILVER-1,0, "mail1@naver.com"),
                new User("b","B","p2", Level.BASIC,MIN_LOGCOUNT_FOR_SILVER,0, "mail1@naver.com"),
                new User("c","C","p3", Level.SILVER,60,MIN_RECOMMEND_FOR_GOLD-1, "mail1@naver.com"),
                new User("d","D","p4", Level.SILVER,60,MIN_RECOMMEND_FOR_GOLD, "mail1@naver.com"),
                new User("e","E","p5", Level.GOLD,100, Integer.MAX_VALUE, "mail1@naver.com")
        );
    }
    @Test
    @DirtiesContext
    public void upgradeLevels() throws Exception {
        userDao.deleteAll();

        for(User user : users) userDao.add(user);

        MockMailSender mockMailSender = new MockMailSender();
        userService.setMailSender(mockMailSender);

        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

//        List<String> request = mockMailSender.getRequests();
//
//        assertThat(request.size(), is(2));
//        assertThat(request.get(0), is(users.get(1).getEmail()));
//        assertThat(request.get(1), is(users.get(3).getEmail()));
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if(upgraded) {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        } else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));

    }

    @Test
    public void add(){
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithOutLevel = users.get(0);

        userWithOutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithOutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithOutLevelRead = userDao.get(userWithOutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithOutLevelRead.getLevel(),is(userWithOutLevel.getLevel()));
    }

    @Test
    public void upgradeAllOrNothing() throws Exception {
        UserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setTransactionManager(this.transactionManager);
        testUserService.setMailSender(this.mailSender);

        userDao.deleteAll();

        for(User user : users) userDao.add(user);

        try{
            testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e){

        }
        checkLevelUpgraded(users.get(1), false);;
    }

    static class TestUserService extends UserService {

        private String id;

        private TestUserService(String id){
            this.id = id;
        }

        protected void upgradeLevel(User user){
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }


    }
    static class TestUserServiceException extends RuntimeException {
    }

    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<String>();

        public List<String> getRequests(){
            return requests;
        }
        @Override
        public void send(SimpleMailMessage simpleMailMessage) throws MailException {
            requests.add(simpleMailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... simpleMailMessages) throws MailException {

        }
    }
}
