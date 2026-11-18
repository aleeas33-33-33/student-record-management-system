<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Error" scope="request"/>
<jsp:include page="../common/header.jsp"/>
<div class="panel error-panel">
    <h2>&#9888; <c:out value="${title}"/></h2>
    <p><c:out value="${message}"/></p>
    <c:if test="${not empty detail}"><p class="muted small">Details: <code><c:out value="${detail}"/></code></p></c:if>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/dashboard">Back to dashboard</a>
</div>
<jsp:include page="../common/footer.jsp"/>
