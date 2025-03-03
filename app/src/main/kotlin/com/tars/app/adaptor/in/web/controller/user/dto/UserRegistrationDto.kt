package com.tars.app.adaptor.`in`.web.controller.user.dto

data class UserRegistrationDto(
    val email: String,
    val password: String,
    val ssn: String,
    val phoneNumber: String,
    val name: String,
    val address: String?,
    val birthDate: String?
)
