package com.tars.app.adaptor.`in`.auth

import com.tars.auth.TokenFacade
import com.tars.auth.dto.TokenResponse
import org.springframework.stereotype.Component

/**
 * auth TokenFacade를 app 모듈에서 사용하기 위한 어댑터
 */
@Component
class TokenServiceAdapter(
    private val tokenFacade: TokenFacade
) {
    /**
     * 사용자 정보를 기반으로 토큰을 생성합니다.
     * 
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @param roles 사용자 역할
     * @return 생성된 토큰 응답
     */
    fun generateTokens(userId: Long, email: String, roles: Set<String>): TokenResponse {
        return tokenFacade.generateTokens(userId, email, roles)
    }
    
    /**
     * 리프레시 토큰을 사용하여 액세스 토큰을 갱신합니다.
     * 
     * @param refreshToken 리프레시 토큰
     * @return 갱신된 토큰 응답
     */
    fun refreshToken(refreshToken: String): TokenResponse {
        return tokenFacade.refreshToken(refreshToken)
    }
    
    /**
     * 토큰의 유효성을 검증합니다.
     * 
     * @param token 검증할 토큰
     * @return 토큰이 유효하면 true, 아니면 false
     */
    fun validateToken(token: String): Boolean {
        return tokenFacade.validateToken(token)
    }
    
    /**
     * 토큰에서 사용자 이메일을 추출합니다.
     * 
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    fun getUserEmailFromToken(token: String): String {
        return tokenFacade.getUserEmailFromToken(token)
    }
    
    /**
     * 토큰에서 사용자 ID를 추출합니다.
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    fun getUserIdFromToken(token: String): Long {
        return tokenFacade.getUserIdFromToken(token)
    }
} 