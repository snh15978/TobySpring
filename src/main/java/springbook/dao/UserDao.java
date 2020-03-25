package springbook.dao;

import springbook.domain.User;

import javax.sql.DataSource;
import java.util.List;

public interface UserDao {
    void add(User user);
    User get(String id);
    List<User> getAll();
    void deleteAll();
    int getCount();

    void setDataSource(DataSource dataSource);
}
