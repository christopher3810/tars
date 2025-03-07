package com.tars.app.config

import com.tars.auth.TokenFacade
import com.tars.auth.config.JwtConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * JWT 관련 설정 클래스
 */
@Configuration
class JwtConfiguration {
    
    @Value("\${jwt.secret:defaultSecretKeyForJwtAuthenticationPleaseOverrideInProduction}")
    private lateinit var secret: String
    
    @Value("\${jwt.expirationMs:3600000}")
    private var expirationMs: Long = 3600000L
    
    @Value("\${jwt.refreshExpirationMs:86400000}")
    private var refreshExpirationMs: Long = 86400000L
    
    /**
     * JwtConfig 빈 등록
     */
    @Bean
    fun jwtConfig(): JwtConfig {
        return JwtConfig.builder()
            .secret(secret)
            .expirationMs(expirationMs)
            .refreshExpirationMs(refreshExpirationMs)
            .build()
    }
    
    /**
     * TokenFacade 빈 등록
     */
    @Bean
    fun tokenFacade(jwtConfig: JwtConfig): TokenFacade {
        return TokenFacade(jwtConfig)
    }
} 