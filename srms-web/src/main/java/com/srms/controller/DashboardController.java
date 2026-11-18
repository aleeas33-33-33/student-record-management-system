package com.srms.controller;

import com.srms.service.CourseService;
import com.srms.service.DepartmentService;
import com.srms.service.StudentService;
import com.srms.web.SessionTrackingListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class DashboardController {

    @Autowired private StudentService studentService;
    @Autowired private DepartmentService departmentService;
    @Autowired private CourseService courseService;

    @GetMapping("/")
    public String home() { return "redirect:/dashboard"; }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        model.addAttribute("sessionId", session.getId());
        model.addAttribute("sessionCreated", f.format(new Date(session.getCreationTime())));
        model.addAttribute("lastAccessed", f.format(new Date(session.getLastAccessedTime())));
        model.addAttribute("timeoutMinutes", session.getMaxInactiveInterval() / 60);
        model.addAttribute("activeSessions", SessionTrackingListener.activeSessions());
        model.addAttribute("studentCount", studentService.countStudents());
        model.addAttribute("deptCount", departmentService.findAll().size());
        model.addAttribute("courseCount", courseService.findAll().size());
        model.addAttribute("enrollmentCount", studentService.countEnrollments());
        model.addAttribute("recent", studentService.search(null, null, null));
        return "dashboard";
    }
}
