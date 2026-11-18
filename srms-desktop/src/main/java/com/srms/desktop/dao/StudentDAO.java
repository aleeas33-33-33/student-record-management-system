package com.srms.desktop.dao;

import com.srms.desktop.db.DBConnection;
import com.srms.desktop.model.Department;
import com.srms.desktop.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access object for the Swing client. Uses plain JDBC with PreparedStatement
 * for every query (Create, Read, Update, Delete + search) against the shared MySQL DB.
 */
public class StudentDAO {

    private static final String SELECT =
            "SELECT s.student_id, s.enrollment_no, s.first_name, s.last_name, s.email, s.phone, s.gender, "
            + "s.dob, s.year_of_study, s.cgpa, s.city, s.dept_id, d.dept_code "
            + "FROM students s JOIN departments d ON s.dept_id = d.dept_id ";

    /** READ: all students, or those matching the keyword. */
    public List<Student> search(String keyword) throws SQLException {
        String sql = SELECT;
        boolean kw = keyword != null && !keyword.isBlank();
        if (kw) sql += "WHERE s.enrollment_no LIKE ? OR LOWER(s.first_name) LIKE ? OR LOWER(s.last_name) LIKE ? "
                + "OR LOWER(s.email) LIKE ? OR LOWER(s.city) LIKE ? OR d.dept_code LIKE ? ";
        sql += "ORDER BY s.enrollment_no";
        List<Student> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (kw) {
                String p = "%" + keyword.trim().toLowerCase() + "%";
                for (int i = 1; i <= 5; i++) ps.setString(i, p);
                ps.setString(6, "%" + keyword.trim().toUpperCase() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    /** CREATE */
    public int insert(Student s) throws SQLException {
        String sql = "INSERT INTO students (enrollment_no, first_name, last_name, email, phone, gender, dob, "
                + "year_of_study, cgpa, city, dept_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, s);
            int rows = ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setId(keys.getInt(1));
            }
            return rows;
        }
    }

    /** UPDATE */
    public int update(Student s) throws SQLException {
        String sql = "UPDATE students SET enrollment_no=?, first_name=?, last_name=?, email=?, phone=?, gender=?, "
                + "dob=?, year_of_study=?, cgpa=?, city=?, dept_id=? WHERE student_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, s);
            ps.setInt(12, s.getId());
            return ps.executeUpdate();
        }
    }

    /** DELETE (course enrollments are removed by ON DELETE CASCADE) */
    public int delete(int studentId) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE student_id=?")) {
            ps.setInt(1, studentId);
            return ps.executeUpdate();
        }
    }

    public List<Department> departments() throws SQLException {
        List<Department> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT dept_id, dept_code, dept_name FROM departments ORDER BY dept_code");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Department(rs.getInt(1), rs.getString(2), rs.getString(3)));
        }
        return list;
    }

    /** Login check against the users table (SHA-256 hash, same as the web app). */
    public String authenticate(String username, String passwordHash) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT full_name FROM users WHERE username=? AND password_hash=?")) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    private static void bind(PreparedStatement ps, Student s) throws SQLException {
        ps.setString(1, s.getEnrollmentNo());
        ps.setString(2, s.getFirstName());
        ps.setString(3, s.getLastName());
        ps.setString(4, s.getEmail());
        ps.setString(5, s.getPhone());
        ps.setString(6, s.getGender());
        ps.setDate(7, Date.valueOf(s.getDob()));
        ps.setInt(8, s.getYearOfStudy());
        if (s.getCgpa() == null) ps.setNull(9, Types.DECIMAL); else ps.setBigDecimal(9, s.getCgpa());
        if (s.getCity() == null || s.getCity().isBlank()) ps.setNull(10, Types.VARCHAR); else ps.setString(10, s.getCity());
        ps.setInt(11, s.getDeptId());
    }

    private static Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("student_id"));
        s.setEnrollmentNo(rs.getString("enrollment_no"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setGender(rs.getString("gender"));
        s.setDob(rs.getDate("dob").toLocalDate());
        s.setYearOfStudy(rs.getInt("year_of_study"));
        s.setCgpa(rs.getBigDecimal("cgpa"));
        s.setCity(rs.getString("city"));
        s.setDeptId(rs.getInt("dept_id"));
        s.setDeptCode(rs.getString("dept_code"));
        return s;
    }
}
