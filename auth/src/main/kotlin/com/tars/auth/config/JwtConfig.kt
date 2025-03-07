package com.tars.auth.config

/**
 * JWT 설정 클래스
 */
data class JwtConfig(
    val secret: String = "defaultSecretKeyForJwtAuthenticationPleaseOverrideInProduction",
    val expirationMs: Long = 3600000L, // 1시간
    val refreshExpirationMs: Long = 86400000L // 24시간
) {
    /**
     * JwtConfig의 빌더 클래스
     * 
     * JwtConfig의 속성을 변경할 수 있는 빌더 패턴을 제공합니다.
     */
    class Builder(
        private var secret: String = "defaultSecretKeyForJwtAuthenticationPleaseOverrideInProduction",
        private var expirationMs: Long = 3600000L, // 1시간
        private var refreshExpirationMs: Long = 86400000L // 24시간
    ) {
        /**
         * JWT 시크릿 키를 설정합니다.
         */
        fun secret(secret: String) = apply { this.secret = secret }
        
        /**
         * 액세스 토큰 만료 시간을 설정합니다.
         */
        fun expirationMs(expirationMs: Long) = apply { this.expirationMs = expirationMs }
        
        /**
         * 리프레시 토큰 만료 시간을 설정합니다.
         */
        fun refreshExpirationMs(refreshExpirationMs: Long) = apply { this.refreshExpirationMs = refreshExpirationMs }
        
        /**
         * JwtConfig 인스턴스를 생성합니다.
         */
        fun build() = JwtConfig(secret, expirationMs, refreshExpirationMs)
    }
    
    /**
     * 현재 JwtConfig의 속성을 기반으로 Builder 인스턴스를 생성합니다.
     */
    fun toBuilder() = Builder(secret, expirationMs, refreshExpirationMs)
    
    companion object {
        /**
         * 표준 설정의 JwtConfig 인스턴스를 반환합니다.
         * 
         * - 기본 시크릿 키
         * - 액세스 토큰 만료 시간: 1시간(3,600,000ms)
         * - 리프레시 토큰 만료 시간: 24시간(86,400,000ms)
         */
        val standard = JwtConfig()
        
        /**
         * JwtConfig 빌더를 생성합니다.
         */
        fun builder() = Builder()
    }
} 