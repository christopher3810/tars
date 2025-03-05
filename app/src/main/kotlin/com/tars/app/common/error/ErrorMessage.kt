package com.tars.app.common.error

enum class ErrorMessage(val code: String, val message: String) {
    // Validation Errors
    INVALID_EMAIL_FORMAT("VAL001", "Invalid email format: %s"),
    INVALID_PASSWORD_FORMAT("VAL002", "Password must be at least 8 characters long and contain at least one number and one letter"),
    INVALID_SSN_FORMAT("VAL003", "Invalid SSN format"),
    INVALID_PHONE_FORMAT("VAL004", "Invalid phone number format"),
    BLANK_NAME("VAL005", "Name cannot be blank"),
    EMPTY_ROLES("VAL006", "User must have at least one role"),
    INVALID_ROLE_FORMAT("VAL007", "Role must start with ROLE_"),

    // Business Errors
    USER_NOT_FOUND("BUS001", "User not found: %s"),
    DUPLICATE_EMAIL("BUS002", "Cannot register user. Email already exists: %s"),
    INVALID_CREDENTIALS("BUS003", "Invalid credentials"),

    // System Errors
    INTERNAL_ERROR("SYS001", "Internal server error"),
    DATABASE_ERROR("SYS002", "Database operation failed");

    fun format(vararg args: Any): String = message.format(*args)
} 