package com.tars.app.application.user

import com.tars.app.adaptor.`in`.web.controller.user.dto.UserRegistrationDto

interface UserRegistrationUseCase {

    suspend fun registerUser(dto: UserRegistrationDto): Response

    data class Response(
        val userId: Long?
    )

}