package com.tars.app.domain.user.vo

import com.tars.common.error.ErrorMessage
import com.tars.common.util.patternValidator.PatternValidator
import org.springframework.security.crypto.bcrypt.BCrypt

data class Password private constructor(val value: String) {
    companion object {
        fun of(rawPassword: String): Password {
            require(PatternValidator.isValidPassword(rawPassword)) { ErrorMessage.INVALID_PASSWORD_FORMAT.message }
            return Password(BCrypt.hashpw(rawPassword, BCrypt.gensalt()))
        }

        fun fromHashed(hashedPassword: String): Password {
            return Password(hashedPassword)
        }
    }

    fun matches(rawPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword, value)
    }
} 