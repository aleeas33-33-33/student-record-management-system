# Student Record Management System (SRMS)

A full-stack **Advanced Java** project that lets an administrator **add, view, update, delete and search student records**.
It has two front ends that share one MySQL database:

* **Web application**: Servlets, JSP (JSTL + EL), Spring MVC with Spring Dependency Injection, and Hibernate ORM, with login, logout and HttpSession tracking
* **Desktop application**: a Swing GUI that uses plain JDBC (`PreparedStatement`)

> Course: **Advance Java Technology (01CE0308)**, Department of Computer Engineering, Faculty of Engineering and Technology, Marwadi University
> Student: **Vats Kotadia (92610120001)**

| | |
|---|---|
| **GitHub repository** | https://github.com/aleeas33-33-33/student-record-management-system |
| **Live application** | `LIVE_URL`: **TODO**, to be added after deployment |

---

## 1. Technologies and tools

| Layer | Technology |
|---|---|
| Language | Java 17 (built and tested with OpenJDK 21) |
| Web: controller | Java Servlets 4.0 (`LoginServlet`, `LogoutServlet`, `HealthServlet`), a Servlet Filter (`AuthFilter`), an `HttpSessionListener`, and Spring MVC 5.3 `@Controller`s |
| Web: view | JSP 2.3 with **JSTL** (`c:`, `fmt:`, `fn:`) and **EL**, no scriptlets; custom CSS; JavaScript validation |
| Dependency Injection | Spring Framework 5.3.39 (`@Configuration`, `@ComponentScan`, `@Service`, `@Repository`, `@Autowired`, constructor injection) |
| ORM | Hibernate ORM 5.6.15 (`SessionFactory`, HQL, `@Entity`, `@ManyToOne`, `@OneToMany`), HikariCP pool |
| Validation | Bean Validation (Hibernate Validator 6.2) on the server, HTML5 + JavaScript on the client |
| Desktop | Java Swing (Nimbus look and feel), JDBC with `PreparedStatement` |
| Database | MySQL 8 / MariaDB 10.6+ (MySQL Connector/J 8.4) |
| Server | Apache Tomcat 9 (javax namespace) |
| Build | Apache Maven 3.9 |
| Deployment | Docker (Tomcat 9 + embedded MariaDB), Render free web service (`render.yaml`) |
| Tools | NetBeans/IntelliJ/VS Code, Git and GitHub, Chrome |

## 2. Features

**Web application**
- Admin **login** (`LoginServlet`) with SHA-256 password check, **logout** (`LogoutServlet`) and protection against session fixation
- **Session management:** `AuthFilter` protects every page, and after logout the browser's Back button returns to the login page (no-cache headers). The dashboard shows the logged-in user, login time, session ID, creation and last-access time, timeout, pages viewed and active sessions (`HttpSessionListener`). There is also a "last user" cookie.
- **Students:** full CRUD (create, list, view, edit, delete) plus search by name, enrollment number, email or city, and filters by department and year
- **Departments:** add, edit and delete. A department that still has students or courses cannot be deleted (foreign key).
- **Courses:** add and delete, and **course enrollment with marks**, where the grade is calculated automatically (A+ to F)
- **Validation:** HTML5 + JavaScript on the client, and Bean Validation plus business rules on the server (duplicate enrollment number or email, in-use records), with clear messages on every field
- Friendly error pages (404, 500, database errors through `@ControllerAdvice`)
- Navigation bar that connects the Dashboard, Students, Add Student, Departments and Courses pages

**Desktop application (Swing + JDBC)**
- Login screen that checks the same `users` table
- `JTable` of all students with live search
- Add, update, delete and clear form with input validation dialogs
- Friendly handling of SQL errors (duplicate key, foreign key, no connection)

## 3. Project structure

```
srms/
├── database/
│   ├── schema.sql            # CREATE DATABASE srms_db, 5 related tables, seed data
│   └── create_user.sql       # optional MySQL user srms_user
├── srms-web/                 # Maven WAR: Servlet/JSP/Spring/Hibernate web app
│   └── src/main/
│       ├── java/com/srms/
│       │   ├── config/       # AppConfig, HibernateConfig, WebConfig (Spring Java config)
│       │   ├── model/        # @Entity: User, Department, Course, Student, Enrollment
│       │   ├── dao/          # @Repository DAO interfaces + Hibernate implementations
│       │   ├── service/      # @Service business logic + @Transactional
│       │   ├── controller/   # Spring MVC @Controllers + GlobalExceptionHandler
│       │   ├── web/          # LoginServlet, LogoutServlet, AuthFilter, SessionTrackingListener
│       │   └── util/         # PasswordUtil (SHA-256)
│       ├── resources/db.properties
│       └── webapp/           # WEB-INF/web.xml, JSP views, static css/js
├── srms-desktop/             # Maven JAR: Swing + JDBC client
│   └── src/main/java/com/srms/desktop/{db,dao,model,ui,util}
├── deploy/                   # docker-entrypoint.sh, low-memory MariaDB config
├── docs/                     # screenshots, ER diagram, DB evidence
├── Dockerfile                # Tomcat 9 + WAR + embedded MariaDB
└── render.yaml               # Render free-tier blueprint
```

## 4. Database configuration and setup

The database is `srms_db`, with 5 InnoDB tables:

| Table | Primary key | Foreign keys |
|---|---|---|
| `users` | `user_id` | none |
| `departments` | `dept_id` | none |
| `courses` | `course_id` | `dept_id` → `departments(dept_id)` |
| `students` | `student_id` | `dept_id` → `departments(dept_id)` |
| `enrollments` | `enrollment_id` | `student_id` → `students(student_id)` (CASCADE), `course_id` → `courses(course_id)` |

![ER diagram](docs/er-diagram.png)

```bash
# 1. create the database, tables and seed data
mysql -u root -p < database/schema.sql
# 2. (optional) create the application user srms_user / srms@123
mysql -u root -p < database/create_user.sql
```

The connection settings live in `srms-web/src/main/resources/db.properties` and `srms-desktop/src/main/resources/db.properties`
(default `jdbc:mysql://localhost:3306/srms_db`, user `srms_user`, password `srms@123`).
You can override all of them **without rebuilding** by setting these environment variables (or `-D` JVM options):

| Variable | Example |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/srms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | `your-password` |

## 5. Install and run

**Prerequisites:** JDK 17+, Maven 3.9+, MySQL 8 or MariaDB, and Apache Tomcat 9.x (Tomcat 9, not 10, because the app uses the `javax.*` namespace).

### Web application
```bash
cd srms-web
mvn clean package                  # produces target/srms.war
cp target/srms.war $CATALINA_HOME/webapps/
export DB_USER=root DB_PASSWORD=yourpassword     # if not using srms_user
$CATALINA_HOME/bin/startup.sh
# open http://localhost:8080/srms/login
```

### Desktop application
```bash
cd srms-desktop
mvn clean package                  # produces target/srms-desktop-jar-with-dependencies.jar
java -jar target/srms-desktop-jar-with-dependencies.jar
# with other DB credentials:
java -DDB_USER=root -DDB_PASSWORD=yourpassword -jar target/srms-desktop-jar-with-dependencies.jar
```

### Docker (single container, the same image used for deployment)
```bash
docker build -t srms .
docker run -p 8080:8080 srms        # embedded MariaDB is seeded from database/schema.sql
# open http://localhost:8080/login
```
When `DB_URL` is **not** set, the container starts its own MariaDB and loads `database/schema.sql`.
When `DB_URL`, `DB_USER` and `DB_PASSWORD` are set, it connects to that external MySQL instead.
The image reads `PORT` (Render sets it automatically).

### Deploy on Render (free)
1. Push this repository to GitHub.
2. On Render, choose **New → Blueprint** (it uses `render.yaml`), or **New → Web Service → Docker**, and select the repository.
3. Instance type: **Free**. No disk and no paid database are needed.
4. Open the generated URL and log in with the default account below.
   Note: the embedded database resets to the seed data whenever the free instance restarts or redeploys.

## 6. Default logins

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `faculty` | `faculty123` | ADMIN |

Passwords are stored as SHA-256 hashes (`SHA2('admin123',256)`).

## 7. Screenshots

| | |
|---|---|
| Login | ![](docs/screenshots/web_02_login_client_validation.png) |
| Dashboard with session tracking | ![](docs/screenshots/web_04_dashboard_session_tracking.png) |
| Student list | ![](docs/screenshots/web_05_student_list.png) |
| Search | ![](docs/screenshots/web_06_search_results.png) |
| Add student, client-side validation | ![](docs/screenshots/web_08_add_client_validation.png) |
| Add student, server-side validation | ![](docs/screenshots/web_09_add_server_validation.png) |
| Student details and course marks | ![](docs/screenshots/web_12_student_detail_enrollments.png) |
| Update | ![](docs/screenshots/web_14_update_success.png) |
| Delete | ![](docs/screenshots/web_15_delete_success.png) |
| Department in use (FK) | ![](docs/screenshots/web_17_department_delete_blocked.png) |
| Logout | ![](docs/screenshots/web_21_logout.png) |
| Swing login | ![](docs/screenshots/swing_01_login.png) |
| Swing JTable | ![](docs/screenshots/swing_03_main_table.png) |
| Swing validation | ![](docs/screenshots/swing_05_validation_error.png) |
| Swing add | ![](docs/screenshots/swing_06_add_success.png) |

## 8. Author

**Vats Kotadia** (Enrollment No. 92610120001), B.Tech Computer Engineering, Marwadi University.
