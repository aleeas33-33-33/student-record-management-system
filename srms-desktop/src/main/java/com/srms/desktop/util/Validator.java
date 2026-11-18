package com.srms.desktop.util;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/** Input validation rules for the Swing forms (same rules as the web app). */
public final class Validator {
    private Validator() { }

    public static List<String> validateStudent(String enr, String first, String last, String email, String phone,
                                               String gender, String dob, String year, String cgpa, Object dept) {
        List<String> e = new ArrayList<>();
        if (!enr.matches("\\d{11}")) e.add("Enrollment number must be exactly 11 digits");
        if (!first.matches("[A-Za-z ]{2,50}")) e.add("First name: 2-50 letters only");
        if (!last.matches("[A-Za-z ]{2,50}")) e.add("Last name: 2-50 letters only");
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) e.add("Enter a valid email address");
        if (!phone.matches("[6-9]\\d{9}")) e.add("Phone must be a 10-digit Indian mobile number");
        if (gender == null || gender.isBlank()) e.add("Please select gender");
        try {
            LocalDate d = LocalDate.parse(dob);
            if (!d.isBefore(LocalDate.now())) e.add("Date of birth must be in the past");
        } catch (DateTimeParseException ex) {
            e.add("Date of birth must be in yyyy-mm-dd format");
        }
        try {
            int y = Integer.parseInt(year);
            if (y < 1 || y > 4) e.add("Year must be between 1 and 4");
        } catch (NumberFormatException ex) {
            e.add("Year must be a number (1-4)");
        }
        if (!cgpa.isBlank()) {
            try {
                BigDecimal c = new BigDecimal(cgpa);
                if (c.compareTo(BigDecimal.ZERO) < 0 || c.compareTo(BigDecimal.TEN) > 0 || c.scale() > 2)
                    e.add("CGPA must be between 0 and 10 (max 2 decimals)");
            } catch (NumberFormatException ex) {
                e.add("CGPA must be a number");
            }
        }
        if (dept == null) e.add("Please select a department");
        return e;
    }

    public static String sha256(String s) {
        try {
            byte[] h = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** Converts SQL errors into messages a user understands. */
    public static String friendly(java.sql.SQLException ex) {
        String m = ex.getMessage() == null ? "" : ex.getMessage();
        if (ex instanceof java.sql.SQLIntegrityConstraintViolationException || "23000".equals(ex.getSQLState())) {
            if (m.contains("enrollment_no")) return "This enrollment number already exists.";
            if (m.contains("email")) return "This email is already used by another student.";
            if (m.toLowerCase().contains("foreign key")) return "Related record missing or still in use (foreign key).";
            return "Duplicate or invalid data: " + m;
        }
        if (ex.getSQLState() != null && ex.getSQLState().startsWith("08"))
            return "Cannot connect to the database. Is MySQL running?\n" + m;
        return "Database error: " + m;
    }
}
