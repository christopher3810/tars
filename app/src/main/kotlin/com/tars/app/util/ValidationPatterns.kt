package com.tars.app.util

object ValidationPatterns {
    private val EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
    private val PHONE_PATTERN = "^01[0-9]-\\d{4}-\\d{4}$".toRegex()
    private val SSN_PATTERN = "^\\d{6}-[1-4]\\d{6}$".toRegex()

    fun isValidEmail(email: String): Boolean = email.matches(EMAIL_PATTERN)
    
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        if (!phoneNumber.matches(PHONE_PATTERN)) return false
        val digits = phoneNumber.replace("-", "")
        return digits.length == 11 && digits.startsWith("01")
    }
    
    fun isValidSSN(ssn: String): Boolean {
        if (!ssn.matches(SSN_PATTERN)) return false
        val parts = ssn.split("-")
        if (parts.size != 2) return false
        
        val front = parts[0]
        val back = parts[1]
        
        return front.length == 6 && back.length == 7 && back[0] in '1'..'4'
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isDigit() } &&
                password.any { it.isLetter() }
    }
} 