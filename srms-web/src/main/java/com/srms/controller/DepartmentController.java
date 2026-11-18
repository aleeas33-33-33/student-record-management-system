package com.srms.controller;

import com.srms.model.Department;
import com.srms.service.BusinessException;
import com.srms.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public String list(@RequestParam(required = false) Integer edit, Model model) {
        Department form = edit != null ? departmentService.findById(edit) : null;
        model.addAttribute("department", form != null ? form : new Department());
        model.addAttribute("departments", departmentService.findAll());
        return "departments";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("department") Department department, BindingResult result,
                       Model model, RedirectAttributes ra) {
        if (department.getCode() != null) department.setCode(department.getCode().trim().toUpperCase());
        if (!result.hasErrors()) {
            try {
                boolean isNew = department.getId() == null;
                departmentService.save(department);
                ra.addFlashAttribute("success", (isNew ? "Department added: " : "Department updated: ") + department.getCode());
                return "redirect:/departments";
            } catch (BusinessException e) {
                result.rejectValue(e.getField() != null ? e.getField() : "code", "dup", e.getMessage());
            }
        }
        model.addAttribute("errors", FormErrors.of(result));
        model.addAttribute("error", "Please correct the highlighted fields.");
        model.addAttribute("departments", departmentService.findAll());
        return "departments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id, RedirectAttributes ra) {
        try {
            departmentService.delete(id);
            ra.addFlashAttribute("success", "Department deleted");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departments";
    }
}
