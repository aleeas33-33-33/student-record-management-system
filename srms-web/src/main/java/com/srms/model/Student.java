package com.srms.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Hibernate entity mapped to the students table (many students -> one department). */
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer id;

    @NotBlank(message = "Enrollment number is required")
    @Pattern(regexp = "^[0-9]{11}$", message = "Enrollment number must be exactly 11 digits")
    @Column(name = "enrollment_no", nullable = false, unique = true, length = 11)
    private String enrollmentNo;

    @NotBlank(message = "First name is required")
    @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "First name: 2-50 letters only")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[A-Za-z ]{2,50}$", message = "Last name: 2-50 letters only")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Enter a valid email address")
    @Size(max = 100)
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone must be a 10-digit Indian mobile number")
    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @NotBlank(message = "Please select gender")
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female or Other")
    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @NotNull(message = "Year of study is required")
    @Min(value = 1, message = "Year must be between 1 and 4")
    @Max(value = 4, message = "Year must be between 1 and 4")
    @Column(name = "year_of_study", nullable = false)
    private Integer yearOfStudy;

    @DecimalMin(value = "0.00", message = "CGPA must be between 0 and 10")
    @DecimalMax(value = "10.00", message = "CGPA must be between 0 and 10")
    @Digits(integer = 2, fraction = 2, message = "CGPA can have at most 2 decimals")
    @Column(name = "cgpa", precision = 4, scale = 2)
    private BigDecimal cgpa;

    @Size(max = 50, message = "City must be at most 50 characters")
    @Column(name = "city", length = 50)
    private String city;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    @OrderBy("id")
    private List<Enrollment> enrollments = new ArrayList<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    /** Form field for the department drop-down (not a column). */
    @Transient
    @NotNull(message = "Please select a department")
    private Integer departmentId;

    public String getFullName() { return firstName + " " + lastName; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public Integer getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(Integer yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public BigDecimal getCgpa() { return cgpa; }
    public void setCgpa(BigDecimal cgpa) { this.cgpa = cgpa; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
}
