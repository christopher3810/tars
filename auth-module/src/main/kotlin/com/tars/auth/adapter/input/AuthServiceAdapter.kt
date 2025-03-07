package com.tars.auth.adapter.input

import com.tars.auth.dto.LoginRequest
import com.tars.auth.dto.TokenRefreshRequest
import com.tars.auth.dto.TokenResponse
import com.tars.auth.port.input.AuthServicePort
import com.tars.auth.service.AuthService
import com.tars.auth.service.TokenProvider
import org.springframework.stereotype.Service

/**
 * 인증 서비스 입력 포트 어댑터
 * 외부 모듈에서 인증 기능을 사용할 수 있도록 합니다.
 */
@Service
class AuthServiceAdapter(
    private val authService: AuthService,
    private val tokenProvider: TokenProvider
) : AuthServicePort {
    /**
     * 사용자 로그인 처리
     * 
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return 토큰 응답
     */
    override fun login(email: String, password: String): TokenResponse {
        val loginRequest = LoginRequest(email, password)
        return authService.login(loginRequest)
    }
    
    /**
     * 토큰 갱신 처리
     * 
     * @param refreshToken 리프레시 토큰
     * @return 갱신된 토큰 응답
     */
    override fun refreshToken(refreshToken: String): TokenResponse {
        val tokenRefreshRequest = TokenRefreshRequest(refreshToken)
        return authService.refreshToken(tokenRefreshRequest)
    }
    
    /**
     * 토큰 검증
     * 
     * @param token 검증할 토큰
     * @return 토큰이 유효하면 true, 아니면 false
     */
    override fun validateToken(token: String): Boolean {
        return tokenProvider.validateToken(token)
    }
    
    /**
     * 토큰에서 사용자 이메일 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    override fun getUserEmailFromToken(token: String): String {
        return tokenProvider.getUsernameFromJWT(token)
    }
} 