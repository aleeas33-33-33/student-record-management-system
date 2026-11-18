package com.srms.dao;

import com.srms.model.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Hibernate DAO for students: all queries use HQL with bound parameters. */
@Repository
public class StudentDaoImpl implements StudentDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() { return sessionFactory.getCurrentSession(); }

    @Override
    public List<Student> search(String keyword, Integer deptId, Integer year) {
        StringBuilder hql = new StringBuilder("from Student s join fetch s.department d where 1=1");
        boolean hasKw = keyword != null && !keyword.isBlank();
        if (hasKw) {
            hql.append(" and (lower(s.firstName) like :kw or lower(s.lastName) like :kw"
                    + " or s.enrollmentNo like :kw or lower(s.email) like :kw or lower(s.city) like :kw)");
        }
        if (deptId != null) hql.append(" and d.id = :dept");
        if (year != null) hql.append(" and s.yearOfStudy = :yr");
        hql.append(" order by s.enrollmentNo");
        Query<Student> q = session().createQuery(hql.toString(), Student.class);
        if (hasKw) q.setParameter("kw", "%" + keyword.trim().toLowerCase() + "%");
        if (deptId != null) q.setParameter("dept", deptId);
        if (year != null) q.setParameter("yr", year);
        return q.list();
    }

    @Override
    public Student findById(int id) { return session().get(Student.class, id); }

    @Override
    public Student findByEnrollmentNo(String enrollmentNo) {
        return session().createQuery("from Student s where s.enrollmentNo = :e", Student.class)
                .setParameter("e", enrollmentNo).uniqueResult();
    }

    @Override
    public Student findByEmail(String email) {
        return session().createQuery("from Student s where lower(s.email) = :e", Student.class)
                .setParameter("e", email.toLowerCase()).uniqueResult();
    }

    @Override
    public void save(Student s) { session().save(s); }

    @Override
    public void update(Student s) { session().merge(s); }

    @Override
    public void delete(Student s) { session().delete(s); }

    @Override
    public long count() {
        return session().createQuery("select count(s) from Student s", Long.class).uniqueResult();
    }
}
