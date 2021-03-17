package org.geektimes.projects.user.management;

import org.geektimes.projects.user.domain.User;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class JolokiaDemo {


    public static void main(String[] args) throws Exception{
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("jolokia:name=" + "User");
        User user = new User();
        mBeanServer.registerMBean(new UserManager(user), name);
        while (true) {
            Thread.sleep(2000);
            System.out.println(user);
        }
    }
}
