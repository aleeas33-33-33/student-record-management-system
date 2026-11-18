package com.srms.dao;

import com.srms.model.Enrollment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EnrollmentDaoImpl implements EnrollmentDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() { return sessionFactory.getCurrentSession(); }

    @Override
    public List<Enrollment> findByStudent(int studentId) {
        return session().createQuery(
                "from Enrollment e join fetch e.course where e.student.id = :s order by e.course.code", Enrollment.class)
                .setParameter("s", studentId).list();
    }

    @Override
    public Enrollment findById(int id) { return session().get(Enrollment.class, id); }

    @Override
    public boolean exists(int studentId, int courseId) {
        Long n = session().createQuery(
                "select count(e) from Enrollment e where e.student.id = :s and e.course.id = :c", Long.class)
                .setParameter("s", studentId).setParameter("c", courseId).uniqueResult();
        return n != null && n > 0;
    }

    @Override
    public void save(Enrollment e) { session().save(e); }

    @Override
    public void delete(Enrollment e) { session().delete(e); }

    @Override
    public long count() {
        return session().createQuery("select count(e) from Enrollment e", Long.class).uniqueResult();
    }
}
