package org.geektimes.projects.user.sql;

import org.geektimes.projects.user.domain.User;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnectionManager {
    private final Logger logger = Logger.getLogger(DBConnectionManager.class.getName());

    @Resource(name = "jdbc/UserPlatformDB")
    private DataSource dataSource;

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

//    public void setConnection(Connection connection) {
//        this.connection = connection;
//    }
//
//    public Connection getConnection() {
//        return this.connection;
//    }
//
//    public void releaseConnection() {
//        if (this.connection != null) {
//            try {
//                this.connection.close();
//            } catch (SQLException e) {
//                throw new RuntimeException(e.getCause());
//            }
//        }
//    }
    public EntityManager getEntityManager() {
        logger.info("当前 EntityManager 实现类：" + entityManager.getClass().getName());
        return entityManager;
    }
    public Connection getConnection() {
        // 依赖查找
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        if (connection != null) {
            logger.log(Level.INFO, "获取 JNDI 数据库连接成功！");
        }
        return connection;
    }

    public static final String DROP_USERS_TABLE_DDL_SQL = "DROP TABLE users";

    public static final String CREATE_USERS_TABLE_DDL_SQL = "CREATE TABLE users(" +
            "id INT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), " +
            "name VARCHAR(16) NOT NULL, " +
            "password VARCHAR(64) NOT NULL, " +
            "email VARCHAR(64) NOT NULL, " +
            "phoneNumber VARCHAR(64) NOT NULL" +
            ")";

    public static final String INSERT_USER_DML_SQL = "INSERT INTO users(name,password,email,phoneNumber) VALUES " +
            "('A','******','a@gmail.com','1') , " +
            "('B','******','b@gmail.com','2') , " +
            "('C','******','c@gmail.com','3') , " +
            "('D','******','d@gmail.com','4') , " +
            "('E','******','e@gmail.com','5')";


    public static void main(String[] args) throws Exception {

        Connection conn = DriverManager.getConnection("jdbc:derby:USER_INFO;create=true");//连接数据库;
        Statement statement = conn.createStatement();
        //可以获取数据库所有表名
        DatabaseMetaData meta = conn.getMetaData();
        // 删除 users 表
        System.out.println(statement.execute(DROP_USERS_TABLE_DDL_SQL)); // false
        // 创建 users 表
        System.out.println(statement.execute(CREATE_USERS_TABLE_DDL_SQL)); // false
        System.out.println(statement.executeUpdate(INSERT_USER_DML_SQL));  // 5

        // 执行查询语句（DML）
        ResultSet resultSet = statement.executeQuery("SELECT id,name,password,email,phoneNumber FROM users");

//        // BeanInfo
//        BeanInfo userBeanInfo = Introspector.getBeanInfo(User.class, Object.class);
//        // 所有的 Properties 信息
//        for (PropertyDescriptor propertyDescriptor : userBeanInfo.getPropertyDescriptors()) {
//            System.out.println(propertyDescriptor.getName() + " , " + propertyDescriptor.getPropertyType());
//        }
        List<Map<String,Object>> userlist = formatResult(resultSet,User.class);
        conn.close();
        Map<Long, User> res = new HashMap<>();
        for(Map<String,Object> map : userlist){
            User u = new User();
            u.setEmail(map.get("EMAIL").toString());
            u.setId(Long.valueOf(map.get("ID").toString()));
            u.setName(map.get("NAME").toString());
            u.setPhoneNumber(map.get("PHONENUMBER").toString());
            u.setPassword(map.get("PASSWORD").toString());
            res.put(u.getId(),u);
        }
    }

    /**
     * 通过反射将对象封装
     * @param userRs
     * @param clazz
     */
    private static <T> List<Map<String,Object>> formatResult(ResultSet userRs, Class<T> clazz) {
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
