package com.tars.auth.domain.token.type

/**
 * 일회용 토큰의 목적을 정의하는 enum 클래스
 */
enum class TokenPurpose(val value: String) {
    EMAIL_VERIFICATION("EMAIL_VERIFICATION"),
    PASSWORD_RESET("PASSWORD_RESET"),
    ACCOUNT_ACTIVATION("ACCOUNT_ACTIVATION");
    
    companion object {
        fun fromValue(value: String): TokenPurpose? {
            return values().find { it.value == value }
        }
    }
} 