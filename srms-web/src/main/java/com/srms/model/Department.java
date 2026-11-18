package com.srms.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/** Hibernate entity mapped to the departments table. */
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Integer id;

    @NotBlank(message = "Department code is required")
    @Pattern(regexp = "^[A-Z]{2,10}$", message = "Code must be 2-10 capital letters (e.g. CE)")
    @Column(name = "dept_code", nullable = false, unique = true, length = 10)
    private String code;

    @NotBlank(message = "Department name is required")
    @Size(min = 3, max = 100, message = "Name must be 3-100 characters")
    @Column(name = "dept_name", nullable = false, length = 100)
    private String name;

    @Size(max = 100, message = "HOD name must be at most 100 characters")
    @Column(name = "hod_name", length = 100)
    private String hodName;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHodName() { return hodName; }
    public void setHodName(String hodName) { this.hodName = hodName; }
}
