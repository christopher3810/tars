package com.tars.auth.config

/**
 * 표준 JWT 설정 구현체
 *
 * 기본 설정값으로 구성된 JWT 설정 구현체.
 */
object StandardJwtStrategy : JwtConfig {
    /** 기본 시크릿 키 */
    override val secret = "defaultSecretKeyForJwtAuthenticationPleaseOverrideInProduction"
    
    /** 액세스 토큰 만료 시간: 1시간 (밀리초) */
    override val expirationMs = 3600000L
    
    /** 리프레시 토큰 만료 시간: 24시간 (밀리초) */
    override val refreshExpirationMs = 86400000L
} 