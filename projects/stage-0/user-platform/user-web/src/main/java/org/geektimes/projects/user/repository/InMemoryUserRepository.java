package org.geektimes.projects.user.repository;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.geektimes.projects.user.domain.User;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存型 {@link UserRepository} 实现
 *
 * @since 1.0s
 */
public class InMemoryUserRepository implements UserRepository {

    private Map<Long, User> repository = new HashMap<>();

    @Override
    public boolean save(User user) {
        return repository.put(user.getId(), user) == null;
    }

    @Override
    public boolean deleteById(Long userId) {
        return repository.remove(userId) != null;
    }

    @Override
    public boolean update(User user) {
        save(user);
        return true;
    }

    @Override
    public User getById(Long userId) {
        return repository.get(userId);
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        repository = initUserRepository();
        return repository.values()
                .stream()
                .filter(user -> Objects.equals(userName, user.getName())
                        && Objects.equals(password, user.getPassword()))
                .findFirst()
                .get();
    }

    @Override
    public Collection<User> getAll() {
        return repository.values();
    }


    public InMemoryUserRepository() {
        repository = initUserRepository();
    }

    private Map<Long, User> initUserRepository() {
        /**
         * 初始化derby数据库
         */
        Map<Long, User> res = new HashMap<>();

            long id =1;
            try {
                //启动嵌入式数据库
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
                System.out.println("Load the embedded driver");
                Connection conn = DriverManager.getConnection("jdbc:derby:USER_INFO;create=true");//连接数据库;
                Statement st = conn.createStatement();
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet rs = meta.getTables(null, null, null,
                        new String[]{"TABLE"});
                HashSet<String> set = new HashSet<String>();
                while (rs.next()) {
                    set.add(rs.getString("TABLE_NAME"));
                }
               boolean hastable =set.contains("USER_INFO".toUpperCase());
               if(!hastable){
                   String sql = "create table USER_INFO (ID INT NOT NULL,NAME VARCHAR(10) NOT NULL,PASSOERD VARCHAR(10) NOT NULL,EMAIL VARCHAR(20)," +
                           "PHONENUMBER VARCHAR(20) )";
                   st.execute(sql);//建表
                   st.executeUpdate("insert into USER_INFO(ID,NAME) values (1,'hermit','123123','292929@QQ.COM','138000000')");//插入数据
                   st.executeUpdate("insert into USER_INFO(ID,NAME) values (2,'test','123123','92929292@QQ.COM','1381111111')");//插入数据
               }
                ResultSet userRs = st.executeQuery("select * from USER_INFO");//读取刚插入的数据
                if(userRs.wasNull()){
                    st.executeUpdate("insert into USER_INFO(ID,NAME) values (1,'hermit','123123','292929@QQ.COM','138000000')");//插入数据
                    st.executeUpdate("insert into USER_INFO(ID,NAME) values (2,'test','123123','92929292@QQ.COM','1381111111')");//插入数据
                }else{
                    while (userRs.next()){
                        User user = new User();
                        user.setId(id);
                        user.setEmail("222@qq.com");
                        user.setName("GEEKTIME");
                        user.setPassword("test");
                        user.setPhoneNumber("13000000000");
                        res.put(id,user);
                    }
                }
                conn.close();
            }catch (Exception e){
                System.err.println(e.getMessage());
            }

            return res;
    }
}
