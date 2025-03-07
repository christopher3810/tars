package com.tars.auth.dto

/**
 * 토큰 갱신 요청 DTO
 */
data class TokenRefreshRequest(
    val refreshToken: String
) {
    /**
     * 요청 데이터의 유효성을 검증합니다.
     * 
     * @throws IllegalArgumentException 유효하지 않은 입력값이 있을 경우
     */
    fun validate() {
        require(refreshToken.isNotBlank()) { "리프레시 토큰은 필수 입력값입니다." }
    }
} 