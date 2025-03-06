package com.tars.auth.dto

import jakarta.validation.constraints.NotBlank

/**
 * 토큰 갱신 요청 DTO
 */
data class TokenRefreshRequest(
    @field:NotBlank(message = "리프레시 토큰은 필수 입력값입니다.")
    val refreshToken: String
) 