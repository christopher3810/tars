package com.tars.app.domain.user.vo

data class SSN private constructor(val value: String) {
    companion object {
        fun of(ssn: String): SSN {
            require(isValidSSN(ssn)) { "Invalid SSN format" }
            return SSN(ssn)
        }

        private fun isValidSSN(ssn: String): Boolean {
            val ssnRegex = "^\\d{6}-[1-4]\\d{6}$"
            return ssn.matches(ssnRegex.toRegex())
        }
    }

    fun getMasked(): String {
        val parts = value.split("-")
        return "${parts[0]}-${parts[1].first()}******"
    }
} 