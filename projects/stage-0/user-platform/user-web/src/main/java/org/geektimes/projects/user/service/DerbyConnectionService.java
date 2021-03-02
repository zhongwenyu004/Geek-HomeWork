package org.geektimes.projects.user.service;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.InMemoryUserRepository;
import org.geektimes.projects.user.repository.UserRepository;

import java.sql.*;

public class DerbyConnectionService {
    public boolean checkuser(String email, String password) {
        InMemoryUserRepository inMemoryUserRepository = new InMemoryUserRepository();
        User user = inMemoryUserRepository.getByNameAndPassword(email,password);
        return true;
    }
}
