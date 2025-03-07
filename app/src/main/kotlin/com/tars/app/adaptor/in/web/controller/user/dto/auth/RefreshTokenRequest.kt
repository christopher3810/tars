package com.tars.app.adaptor.`in`.web.controller.user.dto.auth

import jakarta.validation.constraints.NotBlank

/**
 * 토큰 갱신 API 요청 DTO
 */
data class RefreshTokenRequest(
    @field:NotBlank(message = "리프레시 토큰은 필수입니다.")
    val refreshToken: String
) 