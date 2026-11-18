package com.srms.web;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Session-check filter: every page except /login and static files requires a
 * logged-in HttpSession; otherwise the user is redirected to the login page.
 * Also counts page views per session (session tracking) and disables caching
 * so protected pages are not shown from the browser cache after logout.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        boolean isPublic = path.equals("/login") || path.startsWith("/static/")
                || path.equals("/favicon.ico") || path.equals("/health");
        if (isPublic) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(SessionKeys.USER) == null) {
            resp.sendRedirect(req.getContextPath() + "/login?expired=1");
            return;
        }

        Integer views = (Integer) session.getAttribute(SessionKeys.PAGE_VIEWS);
        session.setAttribute(SessionKeys.PAGE_VIEWS, views == null ? 1 : views + 1);

        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);
        chain.doFilter(request, response);
    }
}
