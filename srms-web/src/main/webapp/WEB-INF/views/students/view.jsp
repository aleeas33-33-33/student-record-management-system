<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Student Details" scope="request"/>
<c:set var="activeNav" value="students" scope="request"/>
<jsp:include page="../common/header.jsp"/>

<div class="page-head">
    <h2><c:out value="${student.fullName}"/> <span class="badge"><c:out value="${student.enrollmentNo}"/></span></h2>
    <div>
        <a class="btn" href="${pageContext.request.contextPath}/students/${student.id}/edit">Edit</a>
        <a class="btn btn-light" href="${pageContext.request.contextPath}/students">&larr; Back</a>
    </div>
</div>

<div class="grid2">
    <section class="panel">
        <h3>Profile</h3>
        <table class="kv">
            <tr><th>Department</th><td><c:out value="${student.department.code}"/> - <c:out value="${student.department.name}"/></td></tr>
            <tr><th>Email</th><td><c:out value="${student.email}"/></td></tr>
            <tr><th>Mobile</th><td><c:out value="${student.phone}"/></td></tr>
            <tr><th>Gender</th><td><c:out value="${student.gender}"/></td></tr>
            <tr><th>Date of birth</th><td>${student.dob}</td></tr>
            <tr><th>Year of study</th><td>${student.yearOfStudy}</td></tr>
            <tr><th>CGPA</th><td><c:out value="${student.cgpa}" default="-"/></td></tr>
            <tr><th>City</th><td><c:out value="${student.city}" default="-"/></td></tr>
        </table>
    </section>
    <section class="panel">
        <h3>Enroll in a course</h3>
        <form method="post" action="${pageContext.request.contextPath}/students/${student.id}/enroll" class="form-inline"
              onsubmit="return validateEnroll(this)" novalidate>
            <select name="courseId" required data-msg="Select a course">
                <option value="">-- Course --</option>
                <c:forEach items="${courses}" var="co">
                    <option value="${co.id}"><c:out value="${co.code}"/> - <c:out value="${co.name}"/></option>
                </c:forEach>
            </select>
            <input name="marks" type="number" min="0" max="100" placeholder="Marks (0-100)" data-msg="Marks must be 0-100">
            <button class="btn btn-primary" type="submit">Enroll</button>
        </form>
        <small class="err" id="enroll-err"></small>
    </section>
</div>

<section class="panel">
    <h3>Course enrollments &amp; marks</h3>
    <c:choose>
        <c:when test="${empty enrollments}"><div class="empty">Not enrolled in any course yet.</div></c:when>
        <c:otherwise>
            <table class="table">
                <thead><tr><th>Course code</th><th>Course</th><th>Sem</th><th>Credits</th><th>Marks</th><th>Grade</th><th>Enrolled on</th><th></th></tr></thead>
                <tbody>
                <c:set var="total" value="0"/><c:set var="cnt" value="0"/>
                <c:forEach items="${enrollments}" var="e">
                    <tr>
                        <td><code><c:out value="${e.course.code}"/></code></td>
                        <td><c:out value="${e.course.name}"/></td>
                        <td>${e.course.semester}</td>
                        <td>${e.course.credits}</td>
                        <td><c:out value="${e.marks}" default="-"/></td>
                        <td><c:out value="${e.grade}" default="-"/></td>
                        <td>${e.enrolledOn}</td>
                        <td class="right">
                            <form method="post" class="inline" action="${pageContext.request.contextPath}/students/${student.id}/enrollments/${e.id}/delete"
                                  onsubmit="return confirm('Remove this enrollment?')">
                                <button class="btn btn-sm btn-danger">Remove</button>
                            </form>
                        </td>
                    </tr>
                    <c:if test="${e.marks != null}"><c:set var="total" value="${total + e.marks}"/><c:set var="cnt" value="${cnt + 1}"/></c:if>
                </c:forEach>
                </tbody>
            </table>
            <c:if test="${cnt > 0}">
                <p class="muted">Average marks: <strong><fmt:formatNumber value="${total / cnt}" maxFractionDigits="2"/></strong> over ${cnt} graded course(s).</p>
            </c:if>
        </c:otherwise>
    </c:choose>
</section>

<jsp:include page="../common/footer.jsp"/>
