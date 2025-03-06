package com.tars.auth.domain.token.builder

import io.jsonwebtoken.JwtBuilder
import java.security.Key
import java.util.UUID

/**
 * 리프레시 토큰 생성을 위한 빌더 구현체
 * 긴 만료 시간을 가진 토큰으로, 액세스 토큰 갱신에 사용됩니다.
 */
class RefreshTokenBuilder(
    subject: String,
    expirationMs: Long,
    key: Key
) : AbstractTokenBuilder(subject, expirationMs, key) {
    
    override fun customizeBuild(builder: JwtBuilder) {
        // 리프레시 토큰 타입 설정
        builder.claim(TokenClaim.TYPE.value, TokenType.REFRESH.value)
        
        // 고유 ID 추가 (토큰 무효화에 사용 가능)
        builder.id(UUID.randomUUID().toString())
    }
} 