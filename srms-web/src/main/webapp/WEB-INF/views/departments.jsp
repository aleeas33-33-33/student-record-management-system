<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Departments" scope="request"/>
<c:set var="activeNav" value="departments" scope="request"/>
<jsp:include page="common/header.jsp"/>

<h2>Departments</h2>
<div class="grid2">
    <section class="panel">
        <h3>${department.id != null ? 'Edit department' : 'Add department'}</h3>
        <form method="post" action="${pageContext.request.contextPath}/departments/save" onsubmit="return validateGeneric(this)" novalidate>
            <c:if test="${department.id != null}"><input type="hidden" name="id" value="${department.id}"></c:if>
            <div class="field ${not empty errors.code ? 'has-error' : ''}">
                <label for="code">Code *</label>
                <input id="code" name="code" value="<c:out value='${department.code}'/>" required pattern="[A-Z]{2,10}" maxlength="10"
                       placeholder="CE" data-msg="Code must be 2-10 capital letters (e.g. CE)">
                <small class="err" id="code-err"><c:out value="${errors.code}"/></small>
            </div>
            <div class="field ${not empty errors.name ? 'has-error' : ''}">
                <label for="name">Name *</label>
                <input id="name" name="name" value="<c:out value='${department.name}'/>" required minlength="3" maxlength="100"
                       data-msg="Name must be 3-100 characters">
                <small class="err" id="name-err"><c:out value="${errors.name}"/></small>
            </div>
            <div class="field">
                <label for="hodName">Head of department</label>
                <input id="hodName" name="hodName" value="<c:out value='${department.hodName}'/>" maxlength="100">
                <small class="err" id="hodName-err"><c:out value="${errors.hodName}"/></small>
            </div>
            <button class="btn btn-primary" type="submit">${department.id != null ? 'Update' : 'Add'} Department</button>
            <c:if test="${department.id != null}"><a class="btn btn-light" href="${pageContext.request.contextPath}/departments">Cancel</a></c:if>
        </form>
    </section>
    <section class="panel">
        <h3>All departments</h3>
        <table class="table">
            <thead><tr><th>Code</th><th>Name</th><th>HOD</th><th class="right">Actions</th></tr></thead>
            <tbody>
            <c:forEach items="${departments}" var="d">
                <tr>
                    <td><strong><c:out value="${d.code}"/></strong></td>
                    <td><a href="${pageContext.request.contextPath}/students?dept=${d.id}"><c:out value="${d.name}"/></a></td>
                    <td><c:out value="${d.hodName}"/></td>
                    <td class="right nowrap">
                        <a class="btn btn-sm" href="${pageContext.request.contextPath}/departments?edit=${d.id}">Edit</a>
                        <form method="post" class="inline" action="${pageContext.request.contextPath}/departments/${d.id}/delete"
                              onsubmit="return confirm('Delete department ${d.code}?')">
                            <button class="btn btn-sm btn-danger">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </section>
</div>

<jsp:include page="common/footer.jsp"/>
