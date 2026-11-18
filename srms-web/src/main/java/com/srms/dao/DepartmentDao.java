package com.srms.dao;

import com.srms.model.Department;
import java.util.List;

public interface DepartmentDao {
    List<Department> findAll();
    Department findById(int id);
    Department findByCode(String code);
    void save(Department d);
    void update(Department d);
    void delete(Department d);
    long countStudents(int deptId);
    long countCourses(int deptId);
}
