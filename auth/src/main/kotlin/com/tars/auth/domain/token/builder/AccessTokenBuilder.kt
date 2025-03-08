package com.tars.auth.domain.token.builder

import com.tars.auth.domain.token.type.TokenClaim
import com.tars.auth.domain.token.type.TokenType
import io.jsonwebtoken.JwtBuilder
import java.security.Key

/**
 * 액세스 토큰 생성을 위한 빌더 구현체
 * 짧은 만료 시간을 가진 일반적인 인증용 토큰을 생성합니다.
 * 최소한의 기능만 제공합니다.
 */
@Deprecated("Access light 한 Token 으로 별도로 분리를 했었으나 필요성을 느끼지 못하고 있음")
class AccessTokenBuilder(
    subject: String,
    expirationMs: Long,
    key: Key
) : AbstractTokenBuilder(subject, expirationMs, key) {
    
    override fun customizeBuild(builder: JwtBuilder) {
        // 액세스 토큰 타입 설정
        builder.claim(TokenClaim.TYPE.value, TokenType.ACCESS.value)
    }
} 