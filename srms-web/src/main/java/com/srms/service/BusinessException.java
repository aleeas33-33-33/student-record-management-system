package com.srms.service;

/** Thrown by the service layer when a business rule fails (duplicate key, record in use...). */
public class BusinessException extends RuntimeException {
    private final String field;

    public BusinessException(String field, String message) {
        super(message);
        this.field = field;
    }

    public BusinessException(String message) { this(null, message); }

    /** Form field the error belongs to, or null for a global error. */
    public String getField() { return field; }
}
