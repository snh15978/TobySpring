package springbook.dao;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.domain.Level;
import springbook.domain.User;


import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoFactory.class)
//@DirtiesContext
public class UserDaoTest {

    @Autowired
    private UserDao dao;
    @Autowired
    private DataSource dataSource;

    User user1;
    User user2;
    User user3;

    @Before
    public void setUp(){
        System.out.println(this);
        this.user1 = new User("1","a","spring1", Level.BASIC,1,0, "mail1@naver.com");
        this.user2 = new User("2","b","spring2", Level.SILVER,55,10, "mail2@naver.com");
        this.user3 = new User("3","c","spring3", Level.GOLD,100,40, "mail3@naver.com");
    }

    @Test
    public void addAndGet() {

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userget1 = dao.get(user1.getId());
        checkSameUser(userget1, user1);

        User userget2 = dao.get(user2.getId());
        checkSameUser(userget2, user2);
    }

    @Test
    public void count() {



        dao.deleteAll();
        assertThat(dao.getCount(),is(0));
        dao.add(user1);
        assertThat(dao.getCount(),is(1));
        dao.add(user2);
        assertThat(dao.getCount(),is(2));
        dao.add(user3);
        assertThat(dao.getCount(),is(3));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() {
        dao.deleteAll();
        assertThat(dao.getCount(),is(0));

        dao.get("asdf");
    }

    @Test
    public void getAll() {
        dao.deleteAll();

        List<User> users0= dao.getAll();
        assertThat(users0.size(),is(0));

        dao.add(user1);
        List<User> users1= dao.getAll();
        assertThat(users1.size(),is(1));
        checkSameUser(user1,users1.get(0));

        dao.add(user2);
        List<User> users2= dao.getAll();
        assertThat(users2.size(),is(2));
        checkSameUser(user1,users2.get(0));
        checkSameUser(user2,users2.get(1));

        dao.add(user3);
        List<User> users3= dao.getAll();
        assertThat(users3.size(),is(3));
        checkSameUser(user1,users3.get(0));
        checkSameUser(user2,users3.get(1));
        checkSameUser(user3,users3.get(2));

    }

    @Test(expected = DataAccessException.class)
    public void duplicateKey(){
        dao.deleteAll();

        dao.add(user1);
        dao.add(user1);
    }

    @Test
    public void sqlExceptionTranslate(){
        dao.deleteAll();

        try {
            dao.add(user1);
            dao.add(user1);
        } catch (DuplicateKeyException ex) {
            SQLException sqlEx = (SQLException) ex.getRootCause();
            SQLExceptionTranslator set =
                    new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
        }
    }

    @Test
    public void update(){
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);
        user1.setName("가나다");
        user1.setPassword("spring5");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        user1.setEmail("email4@naver.com");
        dao.update(user1);

        User user1update = dao.get(user1.getId());
        User user2same = dao.get(user2.getId());
        checkSameUser(user1, user1update);
        checkSameUser(user2, user2same);
    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
        assertThat(user1.getEmail(), is(user2.getEmail()));
    }
}
