-- =====================================================================
--  Student Record Management System (SRMS) - Database Script
--  Advance Java Technology (01CE0308) - Marwadi University
--  Author : Vats Kotadia (92610120001)
--  Target : MySQL 8.x / MariaDB 10.6+
-- =====================================================================

DROP DATABASE IF EXISTS srms_db;
CREATE DATABASE srms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE srms_db;

-- 1. Application users (admin login for web + desktop)
CREATE TABLE users (
    user_id       INT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash CHAR(64)     NOT NULL,           -- SHA-256 hex
    full_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'ADMIN',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Departments
CREATE TABLE departments (
    dept_id    INT PRIMARY KEY AUTO_INCREMENT,
    dept_code  VARCHAR(10)  NOT NULL UNIQUE,
    dept_name  VARCHAR(100) NOT NULL,
    hod_name   VARCHAR(100)
) ENGINE=InnoDB;

-- 3. Courses (each course belongs to a department)
CREATE TABLE courses (
    course_id   INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(15)  NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    credits     INT          NOT NULL DEFAULT 4,
    semester    INT          NOT NULL,
    dept_id     INT          NOT NULL,
    CONSTRAINT fk_course_dept FOREIGN KEY (dept_id)
        REFERENCES departments(dept_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_course_credits CHECK (credits BETWEEN 1 AND 6),
    CONSTRAINT chk_course_sem CHECK (semester BETWEEN 1 AND 8)
) ENGINE=InnoDB;

-- 4. Students (each student belongs to a department)
CREATE TABLE students (
    student_id    INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_no VARCHAR(11)  NOT NULL UNIQUE,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    phone         VARCHAR(10)  NOT NULL,
    gender        VARCHAR(10)  NOT NULL,
    dob           DATE         NOT NULL,
    year_of_study INT          NOT NULL,
    cgpa          DECIMAL(4,2),
    city          VARCHAR(50),
    dept_id       INT          NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_dept FOREIGN KEY (dept_id)
        REFERENCES departments(dept_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_student_year CHECK (year_of_study BETWEEN 1 AND 4),
    CONSTRAINT chk_student_cgpa CHECK (cgpa IS NULL OR cgpa BETWEEN 0 AND 10)
) ENGINE=InnoDB;

-- 5. Enrollments / marks (student <-> course, many-to-many with data)
CREATE TABLE enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id    INT NOT NULL,
    course_id     INT NOT NULL,
    marks         INT,
    grade         VARCHAR(2),
    enrolled_on   DATE NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_enr_student FOREIGN KEY (student_id)
        REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_enr_course FOREIGN KEY (course_id)
        REFERENCES courses(course_id) ON DELETE RESTRICT,
    CONSTRAINT uq_enr UNIQUE (student_id, course_id),
    CONSTRAINT chk_enr_marks CHECK (marks IS NULL OR marks BETWEEN 0 AND 100)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
--  Seed data
-- ---------------------------------------------------------------------
-- Default login: admin / admin123  (stored as SHA-256 hash)
INSERT INTO users (username, password_hash, full_name, role) VALUES
 ('admin',   SHA2('admin123', 256), 'System Administrator', 'ADMIN'),
 ('faculty', SHA2('faculty123', 256), 'Faculty Coordinator', 'ADMIN');

INSERT INTO departments (dept_code, dept_name, hod_name) VALUES
 ('CE',  'Computer Engineering',              'Dr. R. Mehta'),
 ('IT',  'Information Technology',            'Dr. S. Patel'),
 ('ME',  'Mechanical Engineering',            'Dr. K. Joshi'),
 ('EC',  'Electronics and Communication',     'Dr. A. Shah');

INSERT INTO courses (course_code, course_name, credits, semester, dept_id) VALUES
 ('01CE0308', 'Advance Java Technology',        4, 3, 1),
 ('01CE0302', 'Database Management System',     4, 3, 1),
 ('01CE0304', 'Data Structures',                4, 3, 1),
 ('01IT0305', 'Web Technologies',               3, 3, 2),
 ('01ME0301', 'Thermodynamics',                 4, 3, 3),
 ('01EC0303', 'Digital Electronics',            4, 3, 4);

INSERT INTO students (enrollment_no, first_name, last_name, email, phone, gender, dob, year_of_study, cgpa, city, dept_id) VALUES
 ('92610120001', 'Vats',    'Kotadia', 'vats.kotadia@marwadiuniversity.ac.in',  '9876543210', 'Male',   '2006-03-14', 2, 8.72, 'Rajkot',     1),
 ('92610120002', 'Aarav',   'Shah',    'aarav.shah@marwadiuniversity.ac.in',    '9824012345', 'Male',   '2006-07-22', 2, 8.10, 'Ahmedabad',  1),
 ('92610120003', 'Diya',    'Patel',   'diya.patel@marwadiuniversity.ac.in',    '9898123456', 'Female', '2006-01-05', 2, 9.05, 'Surat',      1),
 ('92610130004', 'Rohan',   'Mehta',   'rohan.mehta@marwadiuniversity.ac.in',   '9712345678', 'Male',   '2005-11-30', 2, 7.45, 'Vadodara',   2),
 ('92610130005', 'Ishita',  'Joshi',   'ishita.joshi@marwadiuniversity.ac.in',  '9601234567', 'Female', '2006-05-18', 2, 8.64, 'Rajkot',     2),
 ('92510140006', 'Karan',   'Trivedi', 'karan.trivedi@marwadiuniversity.ac.in', '9925098765', 'Male',   '2005-02-11', 3, 6.98, 'Jamnagar',   3),
 ('92510150007', 'Priya',   'Desai',   'priya.desai@marwadiuniversity.ac.in',   '9909876543', 'Female', '2005-09-27', 3, 8.31, 'Bhavnagar',  4),
 ('92710120008', 'Neel',    'Raval',   'neel.raval@marwadiuniversity.ac.in',    '9879001122', 'Male',   '2007-04-02', 1, NULL, 'Junagadh',   1);

INSERT INTO enrollments (student_id, course_id, marks, grade) VALUES
 (1, 1, 88, 'A'), (1, 2, 79, 'B+'), (1, 3, 91, 'A+'),
 (2, 1, 74, 'B'), (3, 1, 95, 'A+'), (3, 2, 89, 'A'),
 (4, 4, 68, 'B'), (5, 4, 83, 'A'), (6, 5, 71, 'B'),
 (7, 6, 86, 'A'), (8, 3, NULL, NULL);
