<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Dashboard" scope="request"/>
<c:set var="activeNav" value="dashboard" scope="request"/>
<jsp:include page="common/header.jsp"/>

<h2>Welcome, <c:out value="${sessionScope.fullName}"/>!</h2>

<div class="cards">
    <a class="card stat" href="${pageContext.request.contextPath}/students"><span class="num">${studentCount}</span><span>Students</span></a>
    <a class="card stat" href="${pageContext.request.contextPath}/departments"><span class="num">${deptCount}</span><span>Departments</span></a>
    <a class="card stat" href="${pageContext.request.contextPath}/courses"><span class="num">${courseCount}</span><span>Courses</span></a>
    <div class="card stat"><span class="num">${enrollmentCount}</span><span>Course enrollments</span></div>
</div>

<div class="grid2">
    <section class="panel">
        <h3>Session tracking (HttpSession)</h3>
        <table class="kv">
            <tr><th>Logged-in user</th><td><c:out value="${sessionScope.loggedUser}"/> (<c:out value="${sessionScope.role}"/>)</td></tr>
            <tr><th>Login time</th><td>${sessionScope.loginTime}</td></tr>
            <tr><th>Session ID</th><td><code>${sessionId}</code></td></tr>
            <tr><th>Session created</th><td>${sessionCreated}</td></tr>
            <tr><th>Last accessed</th><td>${lastAccessed}</td></tr>
            <tr><th>Timeout</th><td>${timeoutMinutes} minutes of inactivity</td></tr>
            <tr><th>Pages viewed (this session)</th><td>${sessionScope.pageViews}</td></tr>
            <tr><th>Active sessions on server</th><td>${activeSessions}</td></tr>
        </table>
    </section>
    <section class="panel">
        <h3>Quick actions</h3>
        <ul class="actions">
            <li><a class="btn btn-primary" href="${pageContext.request.contextPath}/students/new">+ Add new student</a></li>
            <li><a class="btn" href="${pageContext.request.contextPath}/students">Search / view all students</a></li>
            <li><a class="btn" href="${pageContext.request.contextPath}/departments">Manage departments</a></li>
            <li><a class="btn" href="${pageContext.request.contextPath}/courses">Manage courses</a></li>
        </ul>
        <h3>Students by year</h3>
        <table class="kv">
            <c:forEach begin="1" end="4" var="y">
                <c:set var="cnt" value="0"/>
                <c:forEach items="${recent}" var="s"><c:if test="${s.yearOfStudy == y}"><c:set var="cnt" value="${cnt + 1}"/></c:if></c:forEach>
                <tr><th>Year ${y}</th><td>${cnt} student(s)</td></tr>
            </c:forEach>
        </table>
    </section>
</div>

<jsp:include page="common/footer.jsp"/>
