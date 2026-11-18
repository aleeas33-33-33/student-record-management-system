<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="isEdit" value="${student.id != null}"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit Student' : 'Add Student'}" scope="request"/>
<c:set var="activeNav" value="${isEdit ? 'students' : 'add'}" scope="request"/>
<jsp:include page="../common/header.jsp"/>

<div class="page-head">
    <h2>${isEdit ? 'Edit Student' : 'Add New Student'}</h2>
    <a class="btn btn-light" href="${pageContext.request.contextPath}/students">&larr; Back to list</a>
</div>

<form id="studentForm" class="panel form-grid" method="post" action="${pageContext.request.contextPath}/students/save"
      onsubmit="return validateStudent(this)" novalidate>
    <c:if test="${isEdit}"><input type="hidden" name="id" value="${student.id}"></c:if>

    <div class="field ${not empty errors.enrollmentNo ? 'has-error' : ''}">
        <label for="enrollmentNo">Enrollment No *</label>
        <input id="enrollmentNo" name="enrollmentNo" value="<c:out value='${student.enrollmentNo}'/>" required
               pattern="[0-9]{11}" maxlength="11" placeholder="11 digits e.g. 92610120001" data-msg="Enrollment number must be exactly 11 digits">
        <small class="err" id="enrollmentNo-err"><c:out value="${errors.enrollmentNo}"/></small>
    </div>
    <div class="field ${not empty errors.departmentId ? 'has-error' : ''}">
        <label for="departmentId">Department *</label>
        <select id="departmentId" name="departmentId" required data-msg="Please select a department">
            <option value="">-- Select department --</option>
            <c:forEach items="${departments}" var="d">
                <option value="${d.id}" ${d.id == student.departmentId ? 'selected' : ''}><c:out value="${d.code}"/> - <c:out value="${d.name}"/></option>
            </c:forEach>
        </select>
        <small class="err" id="departmentId-err"><c:out value="${errors.departmentId}"/></small>
    </div>
    <div class="field ${not empty errors.firstName ? 'has-error' : ''}">
        <label for="firstName">First name *</label>
        <input id="firstName" name="firstName" value="<c:out value='${student.firstName}'/>" required
               pattern="[A-Za-z ]{2,50}" maxlength="50" data-msg="First name: 2-50 letters only">
        <small class="err" id="firstName-err"><c:out value="${errors.firstName}"/></small>
    </div>
    <div class="field ${not empty errors.lastName ? 'has-error' : ''}">
        <label for="lastName">Last name *</label>
        <input id="lastName" name="lastName" value="<c:out value='${student.lastName}'/>" required
               pattern="[A-Za-z ]{2,50}" maxlength="50" data-msg="Last name: 2-50 letters only">
        <small class="err" id="lastName-err"><c:out value="${errors.lastName}"/></small>
    </div>
    <div class="field ${not empty errors.email ? 'has-error' : ''}">
        <label for="email">Email *</label>
        <input id="email" name="email" type="email" value="<c:out value='${student.email}'/>" required maxlength="100"
               placeholder="name@marwadiuniversity.ac.in" data-msg="Enter a valid email address">
        <small class="err" id="email-err"><c:out value="${errors.email}"/></small>
    </div>
    <div class="field ${not empty errors.phone ? 'has-error' : ''}">
        <label for="phone">Mobile *</label>
        <input id="phone" name="phone" value="<c:out value='${student.phone}'/>" required pattern="[6-9][0-9]{9}" maxlength="10"
               placeholder="10-digit mobile" data-msg="Phone must be a 10-digit Indian mobile number">
        <small class="err" id="phone-err"><c:out value="${errors.phone}"/></small>
    </div>
    <div class="field ${not empty errors.gender ? 'has-error' : ''}">
        <label for="gender">Gender *</label>
        <select id="gender" name="gender" required data-msg="Please select gender">
            <option value="">-- Select --</option>
            <c:forEach items="${['Male','Female','Other']}" var="g">
                <option ${g == student.gender ? 'selected' : ''}>${g}</option>
            </c:forEach>
        </select>
        <small class="err" id="gender-err"><c:out value="${errors.gender}"/></small>
    </div>
    <div class="field ${not empty errors.dob ? 'has-error' : ''}">
        <label for="dob">Date of birth *</label>
        <input id="dob" name="dob" type="date" value="${student.dob}" required data-msg="Date of birth is required and must be in the past">
        <small class="err" id="dob-err"><c:out value="${errors.dob}"/></small>
    </div>
    <div class="field ${not empty errors.yearOfStudy ? 'has-error' : ''}">
        <label for="yearOfStudy">Year of study *</label>
        <input id="yearOfStudy" name="yearOfStudy" type="number" min="1" max="4" value="${student.yearOfStudy}" required data-msg="Year must be between 1 and 4">
        <small class="err" id="yearOfStudy-err"><c:out value="${errors.yearOfStudy}"/></small>
    </div>
    <div class="field ${not empty errors.cgpa ? 'has-error' : ''}">
        <label for="cgpa">CGPA (0-10)</label>
        <input id="cgpa" name="cgpa" type="number" step="0.01" min="0" max="10" value="${student.cgpa}" data-msg="CGPA must be between 0 and 10">
        <small class="err" id="cgpa-err"><c:out value="${errors.cgpa}"/></small>
    </div>
    <div class="field ${not empty errors.city ? 'has-error' : ''}">
        <label for="city">City</label>
        <input id="city" name="city" value="<c:out value='${student.city}'/>" maxlength="50">
        <small class="err" id="city-err"><c:out value="${errors.city}"/></small>
    </div>
    <div class="form-actions">
        <button type="submit" class="btn btn-primary">${isEdit ? 'Update Student' : 'Save Student'}</button>
        <button type="reset" class="btn btn-light">Clear</button>
    </div>
</form>

<jsp:include page="../common/footer.jsp"/>
