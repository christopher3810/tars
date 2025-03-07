package com.tars.app.adaptor.`in`.security.service

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/**
 * 커스텀 인증 제공자
 * 
 * 사용자가 입력한 자격 증명(아이디/비밀번호)을 검증하는 로직을 커스터마이징합니다.
 * JWT 인증만 사용하는 경우 이 클래스는 직접적으로 사용되지 않을 수 있지만,
 * 폼 로그인이나 기타 인증 방식을 추가할 경우 필요합니다.
 */
@Component
class CustomAuthenticationProvider(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
) : AuthenticationProvider {
    
    private val log = LoggerFactory.getLogger(this::class.java)
    
    /**
     * 인증 처리 로직입니다.
     * 
     * @param authentication 인증 객체 (보통 UsernamePasswordAuthenticationToken)
     * @return 인증된 Authentication 객체
     * @throws BadCredentialsException 자격 증명이 유효하지 않은 경우
     */
    override fun authenticate(authentication: Authentication): Authentication {
        val email = authentication.name
        val password = authentication.credentials.toString()
        
        log.debug("인증 시도: $email")
        
        // UserDetailsService를 통해 사용자 정보 로드
        val userDetails = userDetailsService.loadUserByUsername(email)
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(password, userDetails.password)) {
            log.debug("비밀번호 불일치: $email")
            throw BadCredentialsException("비밀번호가 일치하지 않습니다.")
        }
        
        // 인증 성공 시 인증된 토큰 반환
        return UsernamePasswordAuthenticationToken(
            userDetails,
            null, // 인증 후에는 credentials를 제거 (보안상 이유)
            userDetails.authorities
        )
    }
    
    /**
     * 이 AuthenticationProvider가 지원하는 Authentication 클래스 확인
     */
    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
} 