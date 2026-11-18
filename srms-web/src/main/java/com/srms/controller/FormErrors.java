package com.srms.controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.LinkedHashMap;
import java.util.Map;

/** Converts Spring/Bean Validation errors into a simple map that JSPs read with EL: ${errors.email}. */
final class FormErrors {
    private FormErrors() { }

    static Map<String, String> of(BindingResult result) {
        Map<String, String> map = new LinkedHashMap<>();
        for (FieldError fe : result.getFieldErrors()) {
            String msg = fe.isBindingFailure()
                    ? "Invalid value for " + fe.getField()
                    : fe.getDefaultMessage();
            map.putIfAbsent(fe.getField(), msg);
        }
        return map;
    }
}
