package com.tars.auth.config

/**
 * JWT 설정 인터페이스
 * 
 * JWT 관련 설정 값을 제공하는 공통 인터페이스입니다.
 */
interface JwtConfig {
    /** JWT 암호화 키 */
    val secret: String
    
    /** 액세스 토큰 만료 시간(ms) */
    val expirationMs: Long
    
    /** 리프레시 토큰 만료 시간(ms) */
    val refreshExpirationMs: Long
    
    companion object {
        /**
         * 표준 JWT 설정을 반환합니다.
         * 
         * @return 기본값으로 설정된 StandardJwtConfig 인스턴스
         */
        fun standard(): JwtConfig = StandardJwtStrategy
        
        /**
         * 커스텀 JWT 설정 빌더를 생성합니다.
         * 
         * @return 새로운 JwtConfigBuilder 인스턴스
         */
        fun builder(): JwtConfigBuilder = JwtConfigBuilder()
    }
} 