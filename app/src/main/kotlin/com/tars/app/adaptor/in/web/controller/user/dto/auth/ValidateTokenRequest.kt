package com.tars.app.adaptor.`in`.web.controller.user.dto.auth

import jakarta.validation.constraints.NotBlank

/**
 * 토큰 검증 API 요청 DTO
 */
data class ValidateTokenRequest(
    @field:NotBlank(message = "토큰은 필수입니다.")
    val token: String
) 