<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${pageTitle}" default="SRMS"/> | Student Record Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <script src="${pageContext.request.contextPath}/static/js/validation.js" defer></script>
</head>
<body>
<header class="topbar">
    <div class="brand"><span class="logo">SRMS</span> Student Record Management System</div>
    <nav>
        <a href="${pageContext.request.contextPath}/dashboard" class="${activeNav == 'dashboard' ? 'active' : ''}">Dashboard</a>
        <a href="${pageContext.request.contextPath}/students" class="${activeNav == 'students' ? 'active' : ''}">Students</a>
        <a href="${pageContext.request.contextPath}/students/new" class="${activeNav == 'add' ? 'active' : ''}">Add Student</a>
        <a href="${pageContext.request.contextPath}/departments" class="${activeNav == 'departments' ? 'active' : ''}">Departments</a>
        <a href="${pageContext.request.contextPath}/courses" class="${activeNav == 'courses' ? 'active' : ''}">Courses</a>
    </nav>
    <div class="userbox">
        <span>&#128100; <c:out value="${sessionScope.fullName}"/> (<c:out value="${sessionScope.loggedUser}"/>)</span>
        <form method="post" action="${pageContext.request.contextPath}/logout" class="inline">
            <button type="submit" class="btn btn-light btn-sm">Logout</button>
        </form>
    </div>
</header>
<main class="container">
    <c:if test="${not empty success}">
        <div class="alert alert-success">&#10004; <c:out value="${success}"/></div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">&#9888; <c:out value="${error}"/></div>
    </c:if>
