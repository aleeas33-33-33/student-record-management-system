package com.srms.dao;

import com.srms.model.Department;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartmentDaoImpl implements DepartmentDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session() { return sessionFactory.getCurrentSession(); }

    @Override
    public List<Department> findAll() {
        return session().createQuery("from Department d order by d.code", Department.class).list();
    }

    @Override
    public Department findById(int id) { return session().get(Department.class, id); }

    @Override
    public Department findByCode(String code) {
        return session().createQuery("from Department d where d.code = :c", Department.class)
                .setParameter("c", code).uniqueResult();
    }

    @Override
    public void save(Department d) { session().save(d); }

    @Override
    public void update(Department d) { session().merge(d); }

    @Override
    public void delete(Department d) { session().delete(d); }

    @Override
    public long countStudents(int deptId) {
        return session().createQuery("select count(s) from Student s where s.department.id = :d", Long.class)
                .setParameter("d", deptId).uniqueResult();
    }

    @Override
    public long countCourses(int deptId) {
        return session().createQuery("select count(c) from Course c where c.department.id = :d", Long.class)
                .setParameter("d", deptId).uniqueResult();
    }
}
