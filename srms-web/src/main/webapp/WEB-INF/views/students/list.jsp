<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Students" scope="request"/>
<c:set var="activeNav" value="students" scope="request"/>
<jsp:include page="../common/header.jsp"/>

<div class="page-head">
    <h2>Students <span class="badge">${fn:length(students)}</span></h2>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/students/new">+ Add Student</a>
</div>

<form class="searchbar" method="get" action="${pageContext.request.contextPath}/students">
    <input type="text" name="q" value="<c:out value='${q}'/>" placeholder="Search name, enrollment no, email or city" maxlength="50">
    <select name="dept">
        <option value="">All departments</option>
        <c:forEach items="${departments}" var="d">
            <option value="${d.id}" ${d.id == dept ? 'selected' : ''}><c:out value="${d.code}"/> - <c:out value="${d.name}"/></option>
        </c:forEach>
    </select>
    <select name="year">
        <option value="">All years</option>
        <c:forEach begin="1" end="4" var="y"><option value="${y}" ${y == year ? 'selected' : ''}>Year ${y}</option></c:forEach>
    </select>
    <button class="btn" type="submit">Search</button>
    <a class="btn btn-light" href="${pageContext.request.contextPath}/students">Reset</a>
</form>

<c:choose>
    <c:when test="${empty students}">
        <div class="empty">No students match your search.</div>
    </c:when>
    <c:otherwise>
        <table class="table">
            <thead>
            <tr><th>#</th><th>Enrollment No</th><th>Name</th><th>Email</th><th>Phone</th><th>Dept</th><th>Year</th><th>CGPA</th><th>City</th><th class="right">Actions</th></tr>
            </thead>
            <tbody>
            <c:forEach items="${students}" var="s" varStatus="st">
                <tr>
                    <td>${st.count}</td>
                    <td><code><c:out value="${s.enrollmentNo}"/></code></td>
                    <td><a href="${pageContext.request.contextPath}/students/${s.id}"><c:out value="${s.fullName}"/></a></td>
                    <td><c:out value="${s.email}"/></td>
                    <td><c:out value="${s.phone}"/></td>
                    <td><c:out value="${s.department.code}"/></td>
                    <td>${s.yearOfStudy}</td>
                    <td><c:choose><c:when test="${s.cgpa != null}"><fmt:formatNumber value="${s.cgpa}" minFractionDigits="2"/></c:when><c:otherwise>&ndash;</c:otherwise></c:choose></td>
                    <td><c:out value="${s.city}"/></td>
                    <td class="right nowrap">
                        <a class="btn btn-sm" href="${pageContext.request.contextPath}/students/${s.id}">View</a>
                        <a class="btn btn-sm" href="${pageContext.request.contextPath}/students/${s.id}/edit">Edit</a>
                        <form method="post" action="${pageContext.request.contextPath}/students/${s.id}/delete" class="inline"
                              onsubmit="return confirm('Delete ${fn:escapeXml(s.fullName)} (${s.enrollmentNo})?')">
                            <button class="btn btn-sm btn-danger" type="submit">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<jsp:include page="../common/footer.jsp"/>
