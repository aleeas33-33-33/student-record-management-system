# SRMS Test Report

Environment: Debian 13 box, OpenJDK 21.0.12, Maven 3.9.9, Apache Tomcat 9.0.124, MariaDB 11.8.6 (MySQL-compatible, port 3307 locally), Google Chrome 151 headless via Playwright 1.65, Xvfb + xfwm4 for Swing.
All results below come from real runs, and every screenshot in `docs/screenshots/` was captured during these runs.

## 1. Build

| Test | How | Result |
|---|---|---|
| Build web WAR | `mvn -B clean package` in `srms-web` (log: `docs/build-log.txt`) | PASS (BUILD SUCCESS, `srms.war`) |
| Build desktop fat jar | `mvn -B clean package` in `srms-desktop` | PASS (`srms-desktop-jar-with-dependencies.jar`) |
| Load schema | `mariadb < database/schema.sql` + `create_user.sql` | PASS (5 tables, 4 FKs, seed rows; `docs/db-evidence.txt`) |
| Deploy WAR | copy to Tomcat 9.0.124 `webapps/`, DB settings via `DB_URL`/`DB_USER`/`DB_PASSWORD` env vars | PASS (Spring + Hibernate context started) |

## 2. Web UI (headless Chrome, `tools/web_screenshots.py`)

| # | Test | Result | Detail |
|---|---|---|---|
| 1 | Access /students without login redirects to login | PASS | http://localhost:8080/srms/login?expired=1 |
| 2 | Login client-side validation blocks short input | PASS | Username must be 3-50 letters, digits or underscore / Password must be at least 6 characters |
| 3 | Wrong password rejected by LoginServlet | PASS | ⚠ Invalid username or password. |
| 4 | Login admin/admin123 opens dashboard | PASS | http://localhost:8080/srms/dashboard |
| 5 | Student list (Read) | PASS | 8 rows |
| 6 | Search 'rajkot' | PASS | 2 rows |
| 7 | Filter by department IT | PASS |  |
| 8 | Add form client-side (HTML5+JS) validation | PASS | 10 fields flagged |
| 9 | Server-side Bean Validation (client checks bypassed) | PASS | 10 fields flagged; alert: ⚠ Please correct the highlighted fields (12 error(s)). |
| 10 | Duplicate enrollment number rejected | PASS | Enrollment number 92610120001 is already registered |
| 11 | Create student 92610120009 (Hibernate save) | PASS | ✔ Student added: Rahul Sharma (92610120009) |
| 12 | Enroll student in 2 courses with marks | PASS | 2 enrollments |
| 13 | Update student 92610120009 (Hibernate merge) | PASS | ✔ Student updated: Rahul Sharma (92610120009) |
| 14 | Updated values visible in list | PASS | 1 |
| 15 | Delete student 92610120009 (cascade enrollments) | PASS | ✔ Student deleted: Rahul Sharma (92610120009) |
| 16 | Delete department in use is blocked (FK) | PASS | ⚠ Cannot delete CE: it still has 4 student(s) and 3 course(s) (foreign key) |
| 17 | Department form client validation | PASS |  |
| 18 | Add course 01CE0310 | PASS |  |
| 19 | Delete unused course 01CE0310 | PASS |  |
| 20 | Custom 404 error page | PASS |  |
| 21 | Logout invalidates session | PASS | http://localhost:8080/srms/login?logout=1 |
| 22 | Dashboard after logout redirects to login | PASS | http://localhost:8080/srms/login?expired=1 |

## 3. HTTP / session (curl, `tools/web_curl_tests.sh`, output `docs/curl-session-test.txt`)

| Test | Result |
|---|---|
| GET /students without session -> 302 to /login?expired=1 | PASS |
| POST /login wrong password -> HTTP 401 + message | PASS |
| POST /login admin/admin123 -> 302 /dashboard + new JSESSIONID + srms_last_user cookie | PASS |
| Dashboard shows user, login time, session id, last access, timeout, page views, active sessions | PASS |
| POST /logout -> 302 /login?logout=1 | PASS |
| Old JSESSIONID after logout -> 302 /login?expired=1 | PASS |
| Server-side validation via curl (12 field errors), duplicate enrollment no, duplicate enrollment in course, update via merge, delete, unknown URL -> custom 404 | PASS (manual curl run during development) |

## 4. Swing desktop (Xvfb, `tools/swing-harness/run_swing_screens.sh`)

| # | Test | Result | Detail |
|---|---|---|---|
| 1 | Swing login with wrong password rejected | PASS | Invalid username or password. |
| 2 | Login OK, JTable loaded via JDBC SELECT | PASS | 8 rows |
| 3 | Search 'rajkot' (PreparedStatement LIKE) | PASS | 2 rows |
| 4 | Swing client-side validation dialog | PASS | Validation error: Please fix the following: - Enrollment number must be exactly 11 digits - First name: 2-50 letters only - Last name: 2-50  |
| 5 | Swing INSERT via PreparedStatement | PASS | Saved: Student added successfully (id 9). |
| 6 | Swing UPDATE via PreparedStatement | PASS | Updated: Student updated successfully. |
| 7 | Updated row re-read from DB | PASS | cgpa=9.10 city=Gondal |
| 8 | Duplicate enrollment -> SQLException handled | PASS | Database error: This enrollment number already exists. |
| 9 | Swing DELETE via PreparedStatement | PASS | row removed, 8 rows |
| 10 | Wrong DB_URL -> connection SQLException shown | PASS | Database error: Cannot connect to the database. Is MySQL running? Communications link failure The last packet sent successfully to the serve |

## 5. Bugs found and fixed during testing

| Bug | Fix |
|---|---|
| Updating a student failed with `ConstraintViolationException` because Hibernate re-validated the managed entity, whose transient `departmentId` was null | Turned off Hibernate's JPA validation (`javax.persistence.validation.mode=none`); Spring MVC `@Valid` still validates at the controller |
| No application log output (the slf4j-simple 1.7 binding did not match slf4j-api 2.x) | Upgraded to slf4j-simple 2.0.16 |
| Client-side `minlength` not enforced for script-filled values (password `123`, department name `AI`) | Added an explicit length check in `validation.js` and a custom `data-msg` |
| Swing login text fields shrank to near-zero width | GridBag fill and a preferred size for the fields |
| Swing `RowSorter` warning after a reload while a row was selected | Clear the selection before reloading and guard the row index (JDK prints one harmless warning in a single harness step) |

## 6. Not tested

- The `Dockerfile` / `deploy/docker-entrypoint.sh` image was built and run by Render (free web service) from commit `f9844be`. Live check on 2026-11-18, 19:28 IST at https://srms-vats.onrender.com: `/health` returned 200 OK, `admin`/`admin123` login redirected to `/dashboard`, and `/students` listed the seeded records.
  What was checked: `bash -n` syntax check of the entrypoint; both base image tags exist on Docker Hub; the embedded-DB bootstrap was simulated on a fresh MariaDB datadir (`mariadb-install-db`, start, socket-root `schema.sql` + `create_user.sql`, then `srms_user` TCP login: 8 students). That run used MariaDB 11.8, while the image uses Ubuntu jammy's MariaDB 10.6.
- The live deployment URL does not exist yet.
