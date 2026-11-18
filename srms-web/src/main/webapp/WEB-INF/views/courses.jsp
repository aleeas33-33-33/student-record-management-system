<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Courses" scope="request"/>
<c:set var="activeNav" value="courses" scope="request"/>
<jsp:include page="common/header.jsp"/>

<h2>Courses</h2>
<div class="grid2 wide-right">
    <section class="panel">
        <h3>Add course</h3>
        <form method="post" action="${pageContext.request.contextPath}/courses/save" onsubmit="return validateGeneric(this)" novalidate>
            <div class="field ${not empty errors.code ? 'has-error' : ''}">
                <label for="code">Course code *</label>
                <input id="code" name="code" value="<c:out value='${course.code}'/>" required pattern="[0-9]{2}[A-Z]{2}[0-9]{4}" maxlength="8"
                       placeholder="01CE0308" data-msg="Code format: 01CE0308">
                <small class="err" id="code-err"><c:out value="${errors.code}"/></small>
            </div>
            <div class="field ${not empty errors.name ? 'has-error' : ''}">
                <label for="name">Course name *</label>
                <input id="name" name="name" value="<c:out value='${course.name}'/>" required minlength="3" maxlength="100" data-msg="Name must be 3-100 characters">
                <small class="err" id="name-err"><c:out value="${errors.name}"/></small>
            </div>
            <div class="field ${not empty errors.credits ? 'has-error' : ''}">
                <label for="credits">Credits *</label>
                <input id="credits" name="credits" type="number" min="1" max="6" value="${course.credits}" required data-msg="Credits must be 1-6">
                <small class="err" id="credits-err"><c:out value="${errors.credits}"/></small>
            </div>
            <div class="field ${not empty errors.semester ? 'has-error' : ''}">
                <label for="semester">Semester *</label>
                <input id="semester" name="semester" type="number" min="1" max="8" value="${course.semester}" required data-msg="Semester must be 1-8">
                <small class="err" id="semester-err"><c:out value="${errors.semester}"/></small>
            </div>
            <div class="field ${not empty errors.departmentId ? 'has-error' : ''}">
                <label for="departmentId">Department *</label>
                <select id="departmentId" name="departmentId" required data-msg="Please select a department">
                    <option value="">-- Select --</option>
                    <c:forEach items="${departments}" var="d">
                        <option value="${d.id}" ${d.id == course.departmentId ? 'selected' : ''}><c:out value="${d.code}"/></option>
                    </c:forEach>
                </select>
                <small class="err" id="departmentId-err"><c:out value="${errors.departmentId}"/></small>
            </div>
            <button class="btn btn-primary" type="submit">Add Course</button>
        </form>
    </section>
    <section class="panel">
        <h3>All courses</h3>
        <table class="table">
            <thead><tr><th>Code</th><th>Name</th><th>Dept</th><th>Sem</th><th>Credits</th><th></th></tr></thead>
            <tbody>
            <c:forEach items="${courses}" var="co">
                <tr>
                    <td><code><c:out value="${co.code}"/></code></td>
                    <td><c:out value="${co.name}"/></td>
                    <td><c:out value="${co.department.code}"/></td>
                    <td>${co.semester}</td>
                    <td>${co.credits}</td>
                    <td class="right">
                        <form method="post" class="inline" action="${pageContext.request.contextPath}/courses/${co.id}/delete"
                              onsubmit="return confirm('Delete course ${co.code}?')">
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
