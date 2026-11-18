<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login | Student Record Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
    <script src="${pageContext.request.contextPath}/static/js/validation.js" defer></script>
</head>
<body class="login-page">
<div class="login-card">
    <div class="login-head">
        <div class="logo big">SRMS</div>
        <h1>Student Record Management System</h1>
        <p class="muted">Marwadi University &middot; Advance Java Technology (01CE0308)</p>
    </div>
    <c:if test="${not empty info}"><div class="alert alert-success"><c:out value="${info}"/></div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger" id="server-error">&#9888; <c:out value="${error}"/></div></c:if>

    <form id="loginForm" method="post" action="${pageContext.request.contextPath}/login"
          onsubmit="return validateLogin(this)" novalidate>
        <div class="field">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" value="<c:out value='${username}'/>"
                   required minlength="3" maxlength="50" pattern="[A-Za-z0-9_]{3,50}" placeholder="admin" autocomplete="username" data-msg="Username must be 3-50 letters, digits or underscore">
            <small class="err" id="username-err"></small>
        </div>
        <div class="field">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required minlength="6" data-msg="Password must be at least 6 characters" placeholder="&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;" autocomplete="current-password">
            <small class="err" id="password-err"></small>
        </div>
        <button type="submit" class="btn btn-primary btn-block">Login</button>
    </form>
    <p class="muted small center">Last user on this browser:
        <c:out value="${empty cookie.srms_last_user ? 'none' : cookie.srms_last_user.value}"/></p>
</div>
</body>
</html>
