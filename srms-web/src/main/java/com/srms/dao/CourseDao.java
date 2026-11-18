package com.srms.dao;

import com.srms.model.Course;
import java.util.List;

public interface CourseDao {
    List<Course> findAll();
    Course findById(int id);
    Course findByCode(String code);
    void save(Course c);
    void delete(Course c);
    long countEnrollments(int courseId);
}
