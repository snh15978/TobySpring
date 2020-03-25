package springbook.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.domain.Level;
import springbook.domain.User;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoFactory.class)
public class UserServiceTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    UserService userService;

    List<User> users;

    @Before
    public void setUp(){
        users = Arrays.asList(
                new User("a","A","p1", Level.BASIC,49,0),
                new User("b","B","p2", Level.BASIC,50,0),
                new User("c","C","p3", Level.SILVER,60,29),
                new User("d","D","p4", Level.SILVER,60,30),
                new User("e","E","p5", Level.GOLD,100, 100)
        );
    }
    @Test
    public void upgradeLevels(){
        userDao.deleteAll();

        for(User user : users) userDao.add(user);

        userService.upgradeLevels();

        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);
    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel(), is(expectedLevel));

    }


}
