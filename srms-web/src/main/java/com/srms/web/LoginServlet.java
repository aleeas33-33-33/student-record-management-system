package com.srms.web;

import com.srms.model.User;
import com.srms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Plain Java Servlet that handles admin login.
 * GET shows the JSP login form, POST validates input (server-side),
 * authenticates through the Spring-managed AuthService and starts an HttpSession.
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    @Autowired
    private AuthService authService;   // injected from the Spring root context

    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        if (s != null && s.getAttribute(SessionKeys.USER) != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        if (req.getParameter("logout") != null) req.setAttribute("info", "You have been logged out successfully.");
        if (req.getParameter("expired") != null) req.setAttribute("error", "Please log in to continue (no active session).");
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = trim(req.getParameter("username"));
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");

        // ---- server-side validation ----
        StringBuilder err = new StringBuilder();
        if (username.isEmpty()) err.append("Username is required. ");
        else if (!username.matches("^[A-Za-z0-9_]{3,50}$")) err.append("Username must be 3-50 letters/digits/underscore. ");
        if (password.isEmpty()) err.append("Password is required. ");
        else if (password.length() < 6) err.append("Password must be at least 6 characters. ");

        User user = null;
        if (err.length() == 0) {
            user = authService.authenticate(username, password);
            if (user == null) err.append("Invalid username or password.");
        }
        if (user == null) {
            req.setAttribute("error", err.toString().trim());
            req.setAttribute("username", username);
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }

        // ---- start a fresh session (prevents session fixation) ----
        HttpSession old = req.getSession(false);
        if (old != null) old.invalidate();
        HttpSession session = req.getSession(true);
        session.setAttribute(SessionKeys.USER, user.getUsername());
        session.setAttribute(SessionKeys.FULL_NAME, user.getFullName());
        session.setAttribute(SessionKeys.ROLE, user.getRole());
        session.setAttribute(SessionKeys.LOGIN_TIME,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        session.setAttribute(SessionKeys.PAGE_VIEWS, 0);
        session.setMaxInactiveInterval(30 * 60);

        Cookie c = new Cookie("srms_last_user", user.getUsername());
        c.setMaxAge(7 * 24 * 3600);
        c.setHttpOnly(true);
        c.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        resp.addCookie(c);

        log("Login success: " + user.getUsername() + " session=" + session.getId());
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
