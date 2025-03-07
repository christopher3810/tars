package com.tars.auth.config

/**
 * JWT 설정 빌더
 * 
 * 커스텀 JWT 설정을 생성하기 위한 빌더 클래스입니다.
 */
class JwtConfigBuilder {
    private var secret: String = StandardJwtStrategy.secret
    private var expirationMs: Long = StandardJwtStrategy.expirationMs
    private var refreshExpirationMs: Long = StandardJwtStrategy.refreshExpirationMs
    
    /** JWT 시크릿 키 설정 */
    fun secret(secret: String) = apply { this.secret = secret }
    
    /** 액세스 토큰 만료 시간 설정 */
    fun expirationMs(expirationMs: Long) = apply { this.expirationMs = expirationMs }
    
    /** 리프레시 토큰 만료 시간 설정 */
    fun refreshExpirationMs(refreshExpirationMs: Long) = apply { this.refreshExpirationMs = refreshExpirationMs }
    
    /**
     * 설정된 값으로 JwtConfig 인스턴스를 생성합니다.
     * 
     * @return 설정된 값을 가진 CustomJwtConfig 인스턴스
     */
    fun build(): JwtConfig = CustomJwtConfig(secret, expirationMs, refreshExpirationMs)
    
    /**
     * 커스텀 JWT 설정 구현체
     */
    private class CustomJwtConfig(
        override val secret: String,
        override val expirationMs: Long,
        override val refreshExpirationMs: Long
    ) : JwtConfig
} 