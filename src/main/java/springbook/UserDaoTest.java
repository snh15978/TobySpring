package springbook;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import springbook.dao.DaoFactory;
import springbook.dao.UserDao;
import springbook.domain.User;

import java.sql.SQLException;

public class UserDaoTest {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        DaoFactory factory = new DaoFactory();
        UserDao dao1 = factory.userDao();
        UserDao dao2 = factory.userDao();

        System.out.println(dao1);
        System.out.println(dao2);
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

        UserDao dao = context.getBean("userDao",UserDao.class);
        UserDao dao3 = context.getBean("userDao", UserDao.class);

        System.out.println(dao);
        System.out.println(dao3);

        User user = new User();

        user.setId("1");
        user.setName("성남현");
        user.setPassword("1234");

        dao.add(user);

        System.out.println(user.getId() + "성공!");

        User user2 = dao.get(user.getId());

        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + " 조회 성공!");

    }
}
