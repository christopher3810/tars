package com.tars.auth.service

import com.tars.auth.dto.LoginRequest
import com.tars.auth.dto.TokenRefreshRequest
import com.tars.auth.dto.TokenResponse
import org.springframework.stereotype.Service

/**
 * 인증 모듈의 기능을 외부 모듈에 제공하는 퍼사드 클래스
 * 
 * 이 클래스는 다른 모듈(예: app 모듈)에서 인증 기능을 직접 사용할 수 있도록 합니다.
 * 컨트롤러 없이 서비스 레이어를 직접 노출하는 방식으로 구현되었습니다.
 */
@Service
class AuthServiceFacade(
    private val authService: AuthService,
    private val tokenProvider: TokenProvider
) {
    /**
     * 사용자 로그인 처리
     * 
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return 토큰 응답
     */
    fun login(email: String, password: String): TokenResponse {
        val loginRequest = LoginRequest(email, password)
        return authService.login(loginRequest)
    }
    
    /**
     * 토큰 갱신 처리
     * 
     * @param refreshToken 리프레시 토큰
     * @return 갱신된 토큰 응답
     */
    fun refreshToken(refreshToken: String): TokenResponse {
        val tokenRefreshRequest = TokenRefreshRequest(refreshToken)
        return authService.refreshToken(tokenRefreshRequest)
    }
    
    /**
     * 토큰 검증
     * 
     * @param token 검증할 토큰
     * @return 토큰이 유효하면 true, 아니면 false
     */
    fun validateToken(token: String): Boolean {
        return tokenProvider.validateToken(token)
    }
    
    /**
     * 토큰에서 사용자 이메일 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    fun getUserEmailFromToken(token: String): String {
        return tokenProvider.getUsernameFromJWT(token)
    }
} 