package com.tars.auth.domain.token.type

/**
 * 토큰 타입을 정의하는 enum 클래스
 */
enum class TokenType(val value: String) {
    ACCESS("access"),
    REFRESH("refresh"),
    AUTHORIZATION("authorization"),
    ONE_TIME("one_time");
    
    companion object {
        fun fromValue(value: String): TokenType? {
            return values().find { it.value == value }
        }
    }
} 