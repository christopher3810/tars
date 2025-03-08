package com.tars.app.domain.user.vo

import com.tars.common.error.ErrorMessage
import com.tars.common.util.masking.MaskingUtil
import com.tars.common.util.patternValidator.PatternValidator
import org.springframework.security.crypto.bcrypt.BCrypt
import java.time.LocalDate

data class UserCredentials private constructor(
    val email: String,
    val hashedPassword: String,
    val ssn: String,
    val phoneNumber: String,
    val name: String,
    val birthDate: LocalDate?
) {
    companion object {
        fun create(
            email: String,
            rawPassword: String,
            ssn: String,
            phoneNumber: String,
            name: String,
            birthDate: LocalDate?
        ): UserCredentials {
            require(PatternValidator.isValidEmail(email)) { ErrorMessage.INVALID_EMAIL_FORMAT.format(email) }
            require(PatternValidator.isValidPassword(rawPassword)) { ErrorMessage.INVALID_PASSWORD_FORMAT.message }
            require(PatternValidator.isValidSSN(ssn)) { ErrorMessage.INVALID_SSN_FORMAT.message }
            require(PatternValidator.isValidPhoneNumber(phoneNumber)) { ErrorMessage.INVALID_PHONE_FORMAT.message }
            require(name.isNotBlank()) { ErrorMessage.BLANK_NAME.message }

            val hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt())
            return UserCredentials(
                email = email,
                hashedPassword = hashedPassword,
                ssn = ssn,
                phoneNumber = phoneNumber,
                name = name,
                birthDate = birthDate
            )
        }

        fun reconstitute(
            email: String,
            hashedPassword: String,
            ssn: String,
            phoneNumber: String,
            name: String,
            birthDate: LocalDate?
        ): UserCredentials {
            require(PatternValidator.isValidEmail(email)) { ErrorMessage.INVALID_EMAIL_FORMAT.format(email) }
            require(PatternValidator.isValidSSN(ssn)) { ErrorMessage.INVALID_SSN_FORMAT.message }
            require(PatternValidator.isValidPhoneNumber(phoneNumber)) { ErrorMessage.INVALID_PHONE_FORMAT.message }
            require(name.isNotBlank()) { ErrorMessage.BLANK_NAME.message }

            return UserCredentials(
                email = email,
                hashedPassword = hashedPassword,
                ssn = ssn,
                phoneNumber = phoneNumber,
                name = name,
                birthDate = birthDate
            )
        }
    }

    fun verifyPassword(rawPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword, hashedPassword)
    }

    fun getMaskedSSN(): String = MaskingUtil.maskSSN(ssn)
    
    fun getMaskedPhoneNumber(): String = MaskingUtil.maskPhone(phoneNumber)
} 