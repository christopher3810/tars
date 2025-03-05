package com.tars.app.domain.user.vo

import com.tars.app.common.error.ErrorMessage
import com.tars.app.util.ValidationPatterns

data class Email private constructor(val value: String) {
    companion object {
        fun of(email: String): Email {
            require(ValidationPatterns.isValidEmail(email)) { ErrorMessage.INVALID_EMAIL_FORMAT.format(email) }
            return Email(email)
        }
    }

    override fun toString(): String = value
} 