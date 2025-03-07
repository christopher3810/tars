package com.tars.auth.dto

/**
 * 토큰 응답 DTO
 */
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 3600 // 기본 만료 시간 1시간 (초 단위)
) 