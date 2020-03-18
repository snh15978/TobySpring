package springbook.dao;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class TestDaoFactory {

//    @Bean
//    public DataSource dataSource(){
//        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//
//        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
//        dataSource.setUrl("jdbc:mysql://localhost/testdb?serverTimezone=UTC");
//        dataSource.setUsername("root");
//        dataSource.setPassword("root");
//
//        return dataSource;
//    }
//    @Bean
//    public UserDao userDao(){
//        UserDao userDao = new UserDao();
//        userDao.setDataSource(dataSource());
//        return userDao;
//    }
//
//    @Bean
//    public ConnectionMaker connectionMaker(){
//        return new DConnectionMaker();
//    }
}
