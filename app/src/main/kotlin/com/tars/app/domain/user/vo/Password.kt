package com.tars.app.domain.user.vo

import org.springframework.security.crypto.bcrypt.BCrypt

data class Password private constructor(val value: String) {
    companion object {
        fun of(rawPassword: String): Password {
            require(isValidPassword(rawPassword)) { "Password must be at least 8 characters long and contain at least one number and one letter" }
            return Password(BCrypt.hashpw(rawPassword, BCrypt.gensalt()))
        }

        fun fromHashed(hashedPassword: String): Password {
            return Password(hashedPassword)
        }

        private fun isValidPassword(password: String): Boolean {
            return password.length >= 8 &&
                    password.any { it.isDigit() } &&
                    password.any { it.isLetter() }
        }
    }

    fun matches(rawPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword, value)
    }
} 