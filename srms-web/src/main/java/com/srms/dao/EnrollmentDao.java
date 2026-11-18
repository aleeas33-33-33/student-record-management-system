package com.srms.dao;

import com.srms.model.Enrollment;
import java.util.List;

public interface EnrollmentDao {
    List<Enrollment> findByStudent(int studentId);
    Enrollment findById(int id);
    boolean exists(int studentId, int courseId);
    void save(Enrollment e);
    void delete(Enrollment e);
    long count();
}
