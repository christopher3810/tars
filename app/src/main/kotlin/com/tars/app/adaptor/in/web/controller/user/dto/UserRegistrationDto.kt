package com.tars.app.adaptor.`in`.web.controller.user.dto

import java.time.LocalDate

data class UserRegistrationDto(
    val email: String,
    val password: String,
    val ssn: String,
    val phoneNumber: String,
    val name: String,
    val address: String?,
    val birthDate: LocalDate?
) {
    companion object {
        fun create(
            email: String,
            password: String,
            ssn: String,
            phoneNumber: String,
            name: String,
            address: String? = null,
            birthDate: String? = null
        ): UserRegistrationDto {
            require(email.isNotBlank()) { "이메일은 필수입니다." }
            require(password.isNotBlank()) { "비밀번호는 필수입니다." }
            require(ssn.isNotBlank()) { "주민번호는 필수입니다." }
            require(phoneNumber.isNotBlank()) { "전화번호는 필수입니다." }
            require(name.isNotBlank()) { "이름은 필수입니다." }

            return UserRegistrationDto(
                email = email.trim(),
                password = password,
                ssn = ssn.trim(),
                phoneNumber = phoneNumber.trim(),
                name = name.trim(),
                address = address?.trim(),
                birthDate = birthDate?.let { LocalDate.parse(it) }
            )
        }
    }
}
