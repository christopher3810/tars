package com.tars.auth.domain.token.builder

import com.tars.auth.domain.token.type.TokenClaim
import com.tars.auth.domain.token.type.TokenType
import io.jsonwebtoken.JwtBuilder
import java.security.Key

/**
 * 권한 검증용 토큰 생성을 위한 빌더 구현체
 * 사용자의 역할과 권한 정보를 포함하는 토큰입니다.
 * Access 시 사용하길 권장합니다.
 */
class AuthorizationTokenBuilder(
    subject: String,
    expirationMs: Long,
    key: Key
) : AbstractTokenBuilder(subject, expirationMs, key) {
    
    /**
     * 사용자 역할 정보를 토큰에 추가합니다.
     * 
     * @param roles 사용자 역할 목록
     * @return TokenBuilder 인스턴스
     */
    fun withRoles(roles: List<String>): TokenBuilder {
        return withClaim(TokenClaim.ROLES.value, roles.joinToString(","))
    }
    
    /**
     * 사용자 권한 정보를 토큰에 추가합니다.
     * 
     * @param permissions 사용자 권한 목록
     * @return TokenBuilder 인스턴스
     */
    fun withPermissions(permissions: List<String>): TokenBuilder {
        return withClaim(TokenClaim.PERMISSIONS.value, permissions.joinToString(","))
    }
    
    override fun customizeBuild(builder: JwtBuilder) {
        // 권한 검증용 토큰 타입 설정
        builder.claim(TokenClaim.TYPE.value, TokenType.AUTHORIZATION.value)
    }
} 