package com.srms.controller;

import com.srms.model.Course;
import com.srms.service.BusinessException;
import com.srms.service.CourseService;
import com.srms.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired private CourseService courseService;
    @Autowired private DepartmentService departmentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("course", new Course());
        populate(model);
        return "courses";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("course") Course course, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (!result.hasErrors()) {
            try {
                courseService.save(course);
                ra.addFlashAttribute("success", "Course added: " + course.getCode() + " - " + course.getName());
                return "redirect:/courses";
            } catch (BusinessException e) {
                result.rejectValue(e.getField() != null ? e.getField() : "code", "dup", e.getMessage());
            }
        }
        model.addAttribute("errors", FormErrors.of(result));
        model.addAttribute("error", "Please correct the highlighted fields.");
        populate(model);
        return "courses";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id, RedirectAttributes ra) {
        try {
            courseService.delete(id);
            ra.addFlashAttribute("success", "Course deleted");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses";
    }

    private void populate(Model model) {
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("departments", departmentService.findAll());
    }
}
