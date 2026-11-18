package com.srms.model;

import javax.persistence.*;
import java.time.LocalDate;

/** Hibernate entity mapped to the enrollments table (student <-> course with marks). */
@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "marks")
    private Integer marks;

    @Column(name = "grade", length = 2)
    private String grade;

    @Column(name = "enrolled_on", insertable = false, updatable = false)
    private LocalDate enrolledOn;

    /** Grade rule used by the university-style grading in this project. */
    public static String gradeFor(Integer marks) {
        if (marks == null) return null;
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 75) return "B+";
        if (marks >= 65) return "B";
        if (marks >= 55) return "C";
        if (marks >= 40) return "D";
        return "F";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Integer getMarks() { return marks; }
    public void setMarks(Integer marks) { this.marks = marks; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public LocalDate getEnrolledOn() { return enrolledOn; }
}
