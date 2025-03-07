package com.tars.auth

import com.tars.auth.config.JwtConfig
import com.tars.auth.config.JwtConfigBuilder
import com.tars.auth.dto.TokenResponse
import com.tars.auth.dto.UserTokenInfo
import com.tars.auth.service.JwtTokenService
import com.tars.auth.service.TokenProvider

/**
 * 토큰 관련 기능을 외부에 제공하는 Facade 클래스
 */
class TokenFacade(
    private val jwtConfig: JwtConfig = JwtConfig.standard()
) {
    private val tokenProvider = TokenProvider(jwtConfig)
    private val tokenService = JwtTokenService(tokenProvider, jwtConfig)

    /** 사용자 정보 기반 토큰 생성 */
    fun generateTokens(
        userId: Long,
        email: String,
        roles: Set<String> = emptySet(),
        additionalClaims: Map<String, Any> = emptyMap()
    ): TokenResponse {
        val userInfo = UserTokenInfo(
            id = userId,
            email = email,
            roles = roles,
            additionalClaims = additionalClaims
        )
        return tokenService.generateTokens(userInfo)
    }

    /** 리프레시 토큰 갱신 */
    fun refreshToken(refreshToken: String): TokenResponse {
        return tokenService.refreshToken(refreshToken)
    }

    /** 토큰 유효성 검증 */
    fun validateToken(token: String): Boolean {
        return tokenService.validateToken(token)
    }

    /** 토큰에서 사용자 정보 추출 */
    fun getUserInfoFromToken(token: String): UserTokenInfo {
        return tokenService.getUserInfoFromToken(token)
    }

    /** 토큰에서 사용자 이메일 추출 */
    fun getUserEmailFromToken(token: String): String {
        return tokenService.getUserInfoFromToken(token).email
    }

    /** 토큰에서 사용자 ID 추출 */
    fun getUserIdFromToken(token: String): Long {
        return tokenService.getUserInfoFromToken(token).id
    }

    /** 토큰에서 사용자 역할 추출 */
    fun getUserRolesFromToken(token: String): Set<String> {
        return tokenService.getUserInfoFromToken(token).roles
    }
    
    companion object {
        /** 표준 설정 인스턴스 생성 */
        fun standard(): TokenFacade {
            return TokenFacade(JwtConfig.standard())
        }
        
        /** 커스텀 설정 인스턴스 생성 */
        fun custom(configBlock: JwtConfigBuilder.() -> Unit): TokenFacade {
            val builder = JwtConfig.builder()
            builder.apply(configBlock)
            return TokenFacade(builder.build())
        }
    }
}