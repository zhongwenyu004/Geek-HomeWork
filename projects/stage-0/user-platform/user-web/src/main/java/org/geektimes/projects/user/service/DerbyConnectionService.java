package org.geektimes.projects.user.service;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.InMemoryUserRepository;
import org.geektimes.projects.user.repository.UserRepository;

import java.sql.*;

public class DerbyConnectionService {
    public boolean checkuser(String name, String password) {
        InMemoryUserRepository inMemoryUserRepository = new InMemoryUserRepository();
        User user = inMemoryUserRepository.getByNameAndPassword(name,password);
        if(user!=null && name.equals(user.getName()) && password.equals(user.getPassword())){
            return true;
        }else{
            return false;
        }

    }
}
