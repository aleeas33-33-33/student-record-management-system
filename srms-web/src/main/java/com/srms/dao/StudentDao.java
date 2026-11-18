package com.srms.dao;

import com.srms.model.Student;
import java.util.List;

public interface StudentDao {
    List<Student> search(String keyword, Integer deptId, Integer year);
    Student findById(int id);
    Student findByEnrollmentNo(String enrollmentNo);
    Student findByEmail(String email);
    void save(Student s);
    void update(Student s);
    void delete(Student s);
    long count();
}
