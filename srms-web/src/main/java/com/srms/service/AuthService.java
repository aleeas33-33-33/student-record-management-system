package com.srms.service;

import com.srms.dao.UserDao;
import com.srms.model.User;
import com.srms.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Authenticates admin users against the users table (SHA-256 password hash). */
@Service
@Transactional(readOnly = true)
public class AuthService {

    @Autowired
    private UserDao userDao;

    /** @return the user if the credentials are valid, otherwise null. */
    public User authenticate(String username, String password) {
        if (username == null || password == null) return null;
        User u = userDao.findByUsername(username.trim());
        if (u != null && u.getPasswordHash().equalsIgnoreCase(PasswordUtil.sha256(password))) {
            return u;
        }
        return null;
    }
}
