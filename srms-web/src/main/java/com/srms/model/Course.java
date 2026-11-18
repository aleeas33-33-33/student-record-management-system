package com.srms.model;

import javax.persistence.*;
import javax.validation.constraints.*;

/** Hibernate entity mapped to the courses table (many courses -> one department). */
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer id;

    @NotBlank(message = "Course code is required")
    @Pattern(regexp = "^[0-9]{2}[A-Z]{2}[0-9]{4}$", message = "Code format: 01CE0308")
    @Column(name = "course_code", nullable = false, unique = true, length = 15)
    private String code;

    @NotBlank(message = "Course name is required")
    @Size(min = 3, max = 100, message = "Name must be 3-100 characters")
    @Column(name = "course_name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be 1-6")
    @Max(value = 6, message = "Credits must be 1-6")
    @Column(name = "credits", nullable = false)
    private Integer credits;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be 1-8")
    @Max(value = 8, message = "Semester must be 1-8")
    @Column(name = "semester", nullable = false)
    private Integer semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    @Transient
    @NotNull(message = "Please select a department")
    private Integer departmentId;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
}
