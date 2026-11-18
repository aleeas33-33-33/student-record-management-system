<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><title>404 - Page not found | SRMS</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"></head>
<body class="login-page">
<div class="login-card center">
    <div class="logo big">404</div>
    <h1>Page not found</h1>
    <p class="muted">The page <code><c:out value="${requestScope['javax.servlet.error.request_uri']}"/></code> does not exist.</p>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/dashboard">Go to dashboard</a>
</div>
</body></html>
