/* SRMS client-side validation (runs before the form is sent to the server). */
function showErr(input, msg) {
    var box = document.getElementById((input.id || input.name) + '-err');
    var field = input.closest('.field');
    if (box) box.textContent = msg || '';
    if (field) field.classList.toggle('has-error', !!msg);
}

/** Generic HTML5 constraint check with friendly custom messages (data-msg). */
function validateGeneric(form) {
    var ok = true, first = null;
    Array.prototype.forEach.call(form.querySelectorAll('input[name], select[name]'), function (el) {
        if (el.type === 'hidden') return;
        if (typeof el.value === 'string' && el.tagName === 'INPUT' && el.type !== 'number' && el.type !== 'date') el.value = el.value.trim();
        var msg = '';
        if (!el.checkValidity()) {
            msg = el.validity.valueMissing ? (labelOf(el) + ' is required') : (el.dataset.msg || el.validationMessage);
        } else if (el.minLength > 0 && el.value.length > 0 && el.value.length < el.minLength) {
            // minlength is not enforced by checkValidity() for scripted values, so check it explicitly
            msg = el.dataset.msg || (labelOf(el) + ' must be at least ' + el.minLength + ' characters');
        }
        showErr(el, msg);
        if (msg) { ok = false; if (!first) first = el; }
    });
    if (first) first.focus();
    return ok;
}

function labelOf(el) {
    var l = el.id ? document.querySelector('label[for="' + el.id + '"]') : null;
    return l ? l.textContent.replace('*', '').trim() : el.name;
}

function validateLogin(form) { return validateGeneric(form); }

function validateStudent(form) {
    var ok = validateGeneric(form);
    var dob = form.querySelector('#dob');
    if (dob && dob.value) {
        var d = new Date(dob.value), today = new Date();
        var age = (today - d) / (365.25 * 24 * 3600 * 1000);
        if (d >= today) { showErr(dob, 'Date of birth must be in the past'); ok = false; }
        else if (age < 15 || age > 60) { showErr(dob, 'Student age must be between 15 and 60 years'); ok = false; }
    }
    var cgpa = form.querySelector('#cgpa');
    if (cgpa && cgpa.value && !/^\d{1,2}(\.\d{1,2})?$/.test(cgpa.value)) { showErr(cgpa, 'CGPA can have at most 2 decimals'); ok = false; }
    if (!ok) {
        var a = document.getElementById('client-alert');
        if (!a) {
            a = document.createElement('div'); a.id = 'client-alert'; a.className = 'alert alert-danger';
            form.parentNode.insertBefore(a, form);
        }
        a.textContent = '\u26A0 Client-side validation: please fix the highlighted fields before saving.';
    }
    return ok;
}

function validateEnroll(form) {
    var c = form.querySelector('[name=courseId]'), m = form.querySelector('[name=marks]');
    var box = document.getElementById('enroll-err'), msg = '';
    if (!c.value) msg = 'Select a course';
    else if (m.value && (isNaN(m.value) || +m.value < 0 || +m.value > 100)) msg = 'Marks must be between 0 and 100';
    box.textContent = msg;
    return !msg;
}
