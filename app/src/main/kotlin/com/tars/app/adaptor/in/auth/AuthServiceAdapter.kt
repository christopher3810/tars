package com.tars.app.adaptor.`in`.auth

import com.tars.auth.dto.TokenResponse
import com.tars.auth.TokenFacade
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/**
 * 인증 모듈의 기능을 애플리케이션 모듈에서 사용할 수 있도록 하는 어댑터
 * 
 * 이 어댑터는 auth 모듈의 TokenFacade 를 사용하여 인증 관련 기능을 제공.
 */
@Component
class AuthServiceAdapter(
    private val tokenFacade: TokenFacade,
    private val passwordEncoder: PasswordEncoder
) {
    /**
     * 사용자 로그인 처리
     * 
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @param userId 사용자 ID
     * @param roles 사용자 역할
     * @param hashedPassword 저장된 해시 비밀번호
     * @return 토큰 응답
     */
    fun login(
        email: String, 
        password: String, 
        userId: Long, 
        roles: Set<String>, 
        hashedPassword: String
    ): TokenResponse {

        require(passwordEncoder.matches(password, hashedPassword)) { "Invalid credentials" }
        return tokenFacade.generateTokens(userId, email, roles)
    }
    
    /**
     * 토큰 갱신 처리
     * 
     * @param refreshToken 리프레시 토큰
     * @return 갱신된 토큰 응답
     */
    fun refreshToken(refreshToken: String): TokenResponse {
        return tokenFacade.refreshToken(refreshToken)
    }
    
    /**
     * 토큰 검증
     * 
     * @param token 검증할 토큰
     * @return 토큰이 유효하면 true, 아니면 false
     */
    fun validateToken(token: String): Boolean {
        return tokenFacade.validateToken(token)
    }
    
    /**
     * 토큰에서 사용자 이메일 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    fun getUserEmailFromToken(token: String): String {
        return tokenFacade.getUserEmailFromToken(token)
    }
} 