package com.srms.controller;

import com.srms.model.Student;
import com.srms.service.BusinessException;
import com.srms.service.CourseService;
import com.srms.service.DepartmentService;
import com.srms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/** CRUD + search for students, plus course enrollment (marks) management. */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final DepartmentService departmentService;
    private final CourseService courseService;

    @Autowired
    public StudentController(StudentService studentService, DepartmentService departmentService,
                             CourseService courseService) {
        this.studentService = studentService;
        this.departmentService = departmentService;
        this.courseService = courseService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) Integer dept,
                       @RequestParam(required = false) Integer year, Model model) {
        model.addAttribute("students", studentService.search(q, dept, year));
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("q", q);
        model.addAttribute("dept", dept);
        model.addAttribute("year", year);
        return "students/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("departments", departmentService.findAll());
        return "students/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, Model model, RedirectAttributes ra) {
        Student s = studentService.findById(id);
        if (s == null) {
            ra.addFlashAttribute("error", "Student #" + id + " not found");
            return "redirect:/students";
        }
        model.addAttribute("student", s);
        model.addAttribute("departments", departmentService.findAll());
        return "students/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("student") Student student, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                boolean isNew = student.getId() == null;
                studentService.save(student);
                ra.addFlashAttribute("success", (isNew ? "Student added: " : "Student updated: ")
                        + student.getFullName() + " (" + student.getEnrollmentNo() + ")");
                return "redirect:/students";
            } catch (BusinessException e) {
                if (e.getField() != null) result.rejectValue(e.getField(), "duplicate", e.getMessage());
                else result.reject("business", e.getMessage());
            }
        }
        model.addAttribute("errors", FormErrors.of(result));
        if (result.hasGlobalErrors()) model.addAttribute("error", result.getGlobalError().getDefaultMessage());
        else model.addAttribute("error", "Please correct the highlighted fields (" + result.getErrorCount() + " error(s)).");
        model.addAttribute("departments", departmentService.findAll());
        return "students/form";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable int id, Model model, RedirectAttributes ra) {
        Student s = studentService.findById(id);
        if (s == null) {
            ra.addFlashAttribute("error", "Student #" + id + " not found");
            return "redirect:/students";
        }
        model.addAttribute("student", s);
        model.addAttribute("enrollments", studentService.enrollmentsOf(id));
        model.addAttribute("courses", courseService.findAll());
        return "students/view";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id, RedirectAttributes ra) {
        try {
            Student s = studentService.delete(id);
            ra.addFlashAttribute("success", "Student deleted: " + s.getFullName() + " (" + s.getEnrollmentNo() + ")");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/students";
    }

    @PostMapping("/{id}/enroll")
    public String enroll(@PathVariable int id, @RequestParam(required = false) Integer courseId,
                         @RequestParam(required = false) Integer marks, RedirectAttributes ra) {
        try {
            studentService.enroll(id, courseId, marks);
            ra.addFlashAttribute("success", "Course enrollment saved");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/students/" + id;
    }

    @PostMapping("/{id}/enrollments/{eid}/delete")
    public String removeEnrollment(@PathVariable int id, @PathVariable int eid, RedirectAttributes ra) {
        try {
            studentService.removeEnrollment(eid);
            ra.addFlashAttribute("success", "Enrollment removed");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/students/" + id;
    }
}
