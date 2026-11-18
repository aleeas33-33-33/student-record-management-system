package com.srms.service;

import com.srms.dao.CourseDao;
import com.srms.dao.DepartmentDao;
import com.srms.model.Course;
import com.srms.model.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseService {

    private final CourseDao courseDao;
    private final DepartmentDao departmentDao;

    /** Constructor injection (Spring DI). */
    @Autowired
    public CourseService(CourseDao courseDao, DepartmentDao departmentDao) {
        this.courseDao = courseDao;
        this.departmentDao = departmentDao;
    }

    @Transactional(readOnly = true)
    public List<Course> findAll() { return courseDao.findAll(); }

    public void save(Course c) {
        if (courseDao.findByCode(c.getCode()) != null) {
            throw new BusinessException("code", "Course code '" + c.getCode() + "' already exists");
        }
        Department d = departmentDao.findById(c.getDepartmentId());
        if (d == null) throw new BusinessException("departmentId", "Selected department does not exist");
        c.setDepartment(d);
        courseDao.save(c);
    }

    public void delete(int id) {
        Course c = courseDao.findById(id);
        if (c == null) throw new BusinessException("Course not found");
        long n = courseDao.countEnrollments(id);
        if (n > 0) throw new BusinessException("Cannot delete " + c.getCode() + ": " + n + " student(s) are enrolled");
        courseDao.delete(c);
    }
}
