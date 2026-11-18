package com.srms.service;

import com.srms.dao.CourseDao;
import com.srms.dao.DepartmentDao;
import com.srms.dao.EnrollmentDao;
import com.srms.dao.StudentDao;
import com.srms.model.Course;
import com.srms.model.Department;
import com.srms.model.Enrollment;
import com.srms.model.Student;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Business logic for students and their course enrollments (marks). */
@Service
@Transactional
public class StudentService {

    @Autowired private StudentDao studentDao;
    @Autowired private DepartmentDao departmentDao;
    @Autowired private CourseDao courseDao;
    @Autowired private EnrollmentDao enrollmentDao;

    @Transactional(readOnly = true)
    public List<Student> search(String keyword, Integer deptId, Integer year) {
        return studentDao.search(keyword, deptId, year);
    }

    @Transactional(readOnly = true)
    public Student findById(int id) {
        Student s = studentDao.findById(id);
        if (s != null) {
            s.setDepartmentId(s.getDepartment().getId());
            Hibernate.initialize(s.getEnrollments());
        }
        return s;
    }

    /** Create or update a student after checking unique constraints. */
    public void save(Student s) {
        Student byEnr = studentDao.findByEnrollmentNo(s.getEnrollmentNo());
        if (byEnr != null && !byEnr.getId().equals(s.getId())) {
            throw new BusinessException("enrollmentNo", "Enrollment number " + s.getEnrollmentNo() + " is already registered");
        }
        Student byEmail = studentDao.findByEmail(s.getEmail());
        if (byEmail != null && !byEmail.getId().equals(s.getId())) {
            throw new BusinessException("email", "Email " + s.getEmail() + " is already used by another student");
        }
        Department d = departmentDao.findById(s.getDepartmentId());
        if (d == null) throw new BusinessException("departmentId", "Selected department does not exist");
        s.setDepartment(d);
        if (s.getCity() != null && s.getCity().isBlank()) s.setCity(null);
        if (s.getId() == null) {
            studentDao.save(s);
        } else {
            if (studentDao.findById(s.getId()) == null) throw new BusinessException("Student not found");
            studentDao.update(s);
        }
    }

    public Student delete(int id) {
        Student s = studentDao.findById(id);
        if (s == null) throw new BusinessException("Student with id " + id + " not found");
        studentDao.delete(s);   // enrollments removed by ON DELETE CASCADE
        return s;
    }

    @Transactional(readOnly = true)
    public long countStudents() { return studentDao.count(); }

    @Transactional(readOnly = true)
    public long countEnrollments() { return enrollmentDao.count(); }

    @Transactional(readOnly = true)
    public List<Enrollment> enrollmentsOf(int studentId) { return enrollmentDao.findByStudent(studentId); }

    public void enroll(int studentId, Integer courseId, Integer marks) {
        if (courseId == null) throw new BusinessException("Please select a course");
        if (marks != null && (marks < 0 || marks > 100)) throw new BusinessException("Marks must be between 0 and 100");
        Student s = studentDao.findById(studentId);
        Course c = courseDao.findById(courseId);
        if (s == null || c == null) throw new BusinessException("Student or course not found");
        if (enrollmentDao.exists(studentId, courseId)) {
            throw new BusinessException("Student is already enrolled in " + c.getCode());
        }
        Enrollment e = new Enrollment();
        e.setStudent(s);
        e.setCourse(c);
        e.setMarks(marks);
        e.setGrade(Enrollment.gradeFor(marks));
        enrollmentDao.save(e);
    }

    public void removeEnrollment(int enrollmentId) {
        Enrollment e = enrollmentDao.findById(enrollmentId);
        if (e == null) throw new BusinessException("Enrollment not found");
        enrollmentDao.delete(e);
    }
}
