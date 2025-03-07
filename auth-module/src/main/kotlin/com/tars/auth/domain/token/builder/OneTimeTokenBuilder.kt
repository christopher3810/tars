package com.tars.auth.domain.token.builder

import com.tars.auth.domain.token.type.TokenClaim
import com.tars.auth.domain.token.type.TokenType
import io.jsonwebtoken.JwtBuilder
import java.security.Key
import java.util.UUID

/**
 * 일회용 토큰 생성을 위한 빌더 구현체
 * 이메일 인증, 비밀번호 재설정 등 일회성 작업에 사용되는 토큰입니다.
 */
class OneTimeTokenBuilder(
    subject: String,
    expirationMs: Long,
    key: Key,
    private val purpose: String
) : AbstractTokenBuilder(subject, expirationMs, key) {
    
    companion object {
        const val TOKEN_TYPE = "one-time"
        const val CLAIM_PURPOSE = "purpose"
        const val CLAIM_USED = "used"
        
        // 일회용 토큰 목적 상수
        const val PURPOSE_EMAIL_VERIFICATION = "EMAIL_VERIFICATION"
        const val PURPOSE_PASSWORD_RESET = "PASSWORD_RESET"
        const val PURPOSE_ACCOUNT_ACTIVATION = "ACCOUNT_ACTIVATION"
    }
    
    init {
        // 기본적으로 사용되지 않은 상태로 초기화
        withClaim(TokenClaim.USED.value, false)
    }
    
    override fun customizeBuild(builder: JwtBuilder) {
        // 일회용 토큰 타입 설정
        builder.claim(TokenClaim.TYPE.value, TokenType.ONE_TIME.value)
        
        // 토큰 목적 설정
        builder.claim(TokenClaim.PURPOSE.value, purpose)
        
        // 고유 ID 추가 (토큰 추적에 사용)
        builder.setId(UUID.randomUUID().toString())
    }
} 