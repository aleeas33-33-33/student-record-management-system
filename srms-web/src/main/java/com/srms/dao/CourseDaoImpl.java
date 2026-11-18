package com.srms.dao;

import com.srms.model.Course;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseDaoImpl implements CourseDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() { return sessionFactory.getCurrentSession(); }

    @Override
    public List<Course> findAll() {
        return session().createQuery("from Course c join fetch c.department order by c.semester, c.code", Course.class).list();
    }

    @Override
    public Course findById(int id) { return session().get(Course.class, id); }

    @Override
    public Course findByCode(String code) {
        return session().createQuery("from Course c where c.code = :c", Course.class)
                .setParameter("c", code).uniqueResult();
    }

    @Override
    public void save(Course c) { session().save(c); }

    @Override
    public void delete(Course c) { session().delete(c); }

    @Override
    public long countEnrollments(int courseId) {
        return session().createQuery("select count(e) from Enrollment e where e.course.id = :c", Long.class)
                .setParameter("c", courseId).uniqueResult();
    }
}
