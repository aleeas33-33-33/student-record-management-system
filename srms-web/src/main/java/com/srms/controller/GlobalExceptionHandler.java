package com.srms.controller;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/** Turns database / unexpected exceptions into a friendly error page instead of a stack trace. */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({DataAccessException.class, HibernateException.class, TransactionException.class})
    public ModelAndView database(Exception ex) {
        log.error("Database error", ex);
        ModelAndView mv = new ModelAndView("error/error");
        mv.addObject("title", "Database error");
        mv.addObject("message", "The database operation could not be completed. Please check that MySQL is running and try again.");
        mv.addObject("detail", rootMessage(ex));
        return mv;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView generic(Exception ex) {
        log.error("Unexpected error", ex);
        ModelAndView mv = new ModelAndView("error/error");
        mv.addObject("title", "Something went wrong");
        mv.addObject("message", "An unexpected error occurred while processing your request.");
        mv.addObject("detail", rootMessage(ex));
        return mv;
    }

    private static String rootMessage(Throwable t) {
        Throwable r = t;
        while (r.getCause() != null && r.getCause() != r) r = r.getCause();
        return r.getClass().getSimpleName() + ": " + r.getMessage();
    }
}
