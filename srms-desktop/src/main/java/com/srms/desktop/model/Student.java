package com.srms.desktop.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Plain Java object for one row of the students table. */
public class Student {
    private int id;
    private String enrollmentNo, firstName, lastName, email, phone, gender, city;
    private LocalDate dob;
    private int yearOfStudy;
    private BigDecimal cgpa;
    private int deptId;
    private String deptCode;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEnrollmentNo() { return enrollmentNo; }
    public void setEnrollmentNo(String v) { this.enrollmentNo = v; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getGender() { return gender; }
    public void setGender(String v) { this.gender = v; }
    public String getCity() { return city; }
    public void setCity(String v) { this.city = v; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate v) { this.dob = v; }
    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int v) { this.yearOfStudy = v; }
    public BigDecimal getCgpa() { return cgpa; }
    public void setCgpa(BigDecimal v) { this.cgpa = v; }
    public int getDeptId() { return deptId; }
    public void setDeptId(int v) { this.deptId = v; }
    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String v) { this.deptCode = v; }
}
