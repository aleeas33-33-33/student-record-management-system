<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><title>500 - Server error | SRMS</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css"></head>
<body class="login-page">
<div class="login-card center">
    <div class="logo big">500</div>
    <h1>Something went wrong</h1>
    <p class="muted">The server could not complete your request. Please try again.</p>
    <c:if test="${not empty requestScope['javax.servlet.error.message']}">
        <p class="small"><code><c:out value="${requestScope['javax.servlet.error.message']}"/></code></p>
    </c:if>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/dashboard">Go to dashboard</a>
</div>
</body></html>
