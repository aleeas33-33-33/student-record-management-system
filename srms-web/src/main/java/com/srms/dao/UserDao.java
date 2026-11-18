package com.srms.dao;

import com.srms.model.User;

public interface UserDao {
    User findByUsername(String username);
}
