package com.tars.app.domain.user.vo

data class Email private constructor(val value: String) {
    companion object {
        fun of(email: String): Email {
            require(isValidEmail(email)) { "Invalid email format: $email" }
            return Email(email)
        }

        private fun isValidEmail(email: String): Boolean {
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            return email.matches(emailRegex.toRegex())
        }
    }

    override fun toString(): String = value
} 