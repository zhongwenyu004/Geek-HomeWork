package org.geektimes.projects.user.repository;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.geektimes.projects.user.domain.User;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        //repository = initUserRepository();
        User u = null;
        try{
         u = repository.values()
                .stream()
                .filter(user -> Objects.equals(userName, user.getName())
                        && Objects.equals(password, user.getPassword()))
                 .findFirst().get();
        }catch (Exception e){
            System.err.println("没有用户");
        }
         return u;
    }

    @Override
    public Collection<User> getAll() {
        return repository.values();
    }


    public InMemoryUserRepository() {
        repository = initUserRepository();
    }

    private static<T> Map<Long, User> initUserRepository() {
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
                //可以获取数据库所有表名
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet rs = meta.getTables(null, null, null,
                        new String[]{"TABLE"});
                HashSet<String> set = new HashSet<String>();
                while (rs.next()) {
                    set.add(rs.getString("TABLE_NAME"));
                }
               boolean hastable =set.contains("USER_INFO".toUpperCase());
               if(hastable){
                   System.out.println(st.execute("DROP TABLE USER_INFO"));//删除表避免不能重复建表
               }
               String sql = "create table USER_INFO (ID INT NOT NULL,NAME VARCHAR(20) NOT NULL,PASSWORD VARCHAR(10) NOT NULL,EMAIL VARCHAR(20)," +
                           "PHONENUMBER VARCHAR(20) )";
               st.execute(sql);//建表
               st.executeUpdate("insert into USER_INFO(ID,NAME,PASSWORD,EMAIL,PHONENUMBER) values (1,'123@qq.com','111111','292929@QQ.COM','138000000')");//插入数据
               st.executeUpdate("insert into USER_INFO(ID,NAME,PASSWORD,EMAIL,PHONENUMBER) values (2,'222@sina.com','111111','92929292@QQ.COM','1381111111')");//插入数据

                ResultSet userRs = st.executeQuery("select * from USER_INFO");//读取刚插入的数据
                List <Map<String,Object>> list = formatResult(userRs,User.class);
                conn.close();
                for(Map<String,Object> map : list){
                    User u = new User();
                    u.setEmail(map.get("EMAIL").toString());
                    u.setId(Long.valueOf(map.get("ID").toString()));
                    u.setName(map.get("NAME").toString());
                    u.setPhoneNumber(map.get("PHONENUMBER").toString());
                    u.setPassword(map.get("PASSWORD").toString());
                    res.put(u.getId(),u);
                }

            }catch (Exception e){
                System.err.println(e.getMessage());
            }
            return res;
    }

    /**
     * 通过反射将对象封装
     * @param userRs
     * @param clazz
     */
    private static <T> List <Map<String,Object>> formatResult(ResultSet userRs, Class<T> clazz) {
        List<Map<String,Object>> list = new ArrayList<>();
        try{

            //通过反射获取对象的实例
            T t =clazz.getConstructor().newInstance();
            //获取resultSet 的列的信息
            ResultSetMetaData metaData = userRs.getMetaData();
            while (userRs.next()) {
                Map<String,Object> map = new HashMap<>();
                //遍历每一列
                for (int i = 0; i < metaData.getColumnCount(); i++) {

                    //获取列的名字
                    String fName = metaData.getColumnLabel(i + 1);
                    //把赋值后的对象 加入到list集合中
                    map.put(fName,userRs.getObject(fName));

                    /***
                     *  也可以使用这种方式进行反射赋值，但是因为数据库与实体类型不一致 可能会导致报错
                     *
                     *  因为列的名字和我们EMP中的属性名是一样的，所以通过列的名字获得其EMP中属性
                     *  Field field = clazz.getDeclaredField(fName.toLowerCase());
                     *  因为属性是私有的，所有获得其对应的set 方法。set+属性名首字母大写+其他小写
                     *  String setName = "set" + fName.toUpperCase().substring(0, 1) + fName.substring(1).toLowerCase();
                     *  因为属性的类型和set方法的参数类型一致，所以可以获得set方法
                     *  Method setMethod = clazz.getMethod(setName, field.getType());
                     *  执行set方法，把resultSet中的值传入emp中，  再继续循环传值
                     *  setMethod.invoke(t, userRs.getObject(fName));
                     */
                }
                list.add(map);
            }
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return list;
    }
}
