package com.tars.app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * 보안 관련 설정 클래스
 */
@Configuration
class SecurityConfig {
    
    /**
     * 비밀번호 인코더 빈
     * 
     * 사용자 비밀번호를 암호화하고 검증하는 데 사용됩니다.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
} 