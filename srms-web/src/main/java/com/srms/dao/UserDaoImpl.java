package com.srms.dao;

import com.srms.model.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public User findByUsername(String username) {
        return sessionFactory.getCurrentSession()
                .createQuery("from User u where u.username = :u", User.class)
                .setParameter("u", username)
                .uniqueResult();
    }
}
