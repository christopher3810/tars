package com.tars.app.application.user

interface UserRegistrationUseCase {

    fun registerUser(
        email: String,
        rawPassword: String,
        ssn: String,
        phone: String,
        name: String,
        address: String?,
        birthDate: String?
    ): Response

    data class Response(
        val userId: Long?
    )

}