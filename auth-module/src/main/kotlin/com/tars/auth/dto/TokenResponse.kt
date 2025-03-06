package com.tars.auth.dto

/**
 * 토큰 응답 DTO
 */
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer"
) 