package com.tars.app.domain.factory

import com.tars.app.domain.user.User
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class UserFactory() {

    fun createUser(
        email: String,
        rawPassword: String,
        ssn: String,
        phoneNumber: String,
        name: String,
        address: String?,
        birthDateString: String?
    ): User {
        val hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt())
        val parsedDate = parseDate(birthDateString)

        return User(
            email = email,
            password = hashed,
            ssn = ssn,
            phoneNumber = phoneNumber,
            name = name,
            address = address,
            birthDate = parsedDate,
            roles = setOf("ROLE_USER")
        )
    }

    private fun parseDate(dateStr: String?): LocalDate? =
        if (dateStr.isNullOrEmpty()) null else LocalDate.parse(dateStr)

}