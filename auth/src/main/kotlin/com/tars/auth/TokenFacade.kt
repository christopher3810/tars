package com.tars.auth

import com.tars.auth.config.JwtConfig
import com.tars.auth.dto.TokenResponse
import com.tars.auth.dto.UserTokenInfo
import com.tars.auth.service.JwtTokenService
import com.tars.auth.service.TokenProvider

/**
 * 토큰 관련 기능을 외부에 제공하는 Facade 클래스
 *
 * 이 클래스는 auth 내부 구현을 숨기고 간단한 인터페이스만 노출합니다.
 * 외부 모듈에서는 이 클래스를 통해 토큰 관련 기능을 사용할 수 있습니다.
 */
class TokenFacade(
    jwtConfig: JwtConfig = JwtConfig.standard
) {
    private val tokenProvider = TokenProvider(jwtConfig)
    private val tokenService = JwtTokenService(tokenProvider, jwtConfig)

    /**
     * 사용자 정보를 기반으로 토큰을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @param roles 사용자 역할
     * @param additionalClaims 추가 클레임
     * @return 생성된 토큰 응답
     */
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

    /**
     * 리프레시 토큰을 사용하여 액세스 토큰을 갱신합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 갱신된 토큰 응답
     */
    fun refreshToken(refreshToken: String): TokenResponse {
        return tokenService.refreshToken(refreshToken)
    }

    /**
     * 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 토큰
     * @return 토큰이 유효하면 true, 아니면 false
     */
    fun validateToken(token: String): Boolean {
        return tokenService.validateToken(token)
    }

    /**
     * 토큰에서 사용자 정보를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 정보
     */
    fun getUserInfoFromToken(token: String): UserTokenInfo {
        return tokenService.getUserInfoFromToken(token)
    }

    /**
     * 토큰에서 사용자 이메일을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    fun getUserEmailFromToken(token: String): String {
        return tokenService.getUserInfoFromToken(token).email
    }

    /**
     * 토큰에서 사용자 ID를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    fun getUserIdFromToken(token: String): Long {
        return tokenService.getUserInfoFromToken(token).id
    }

    /**
     * 토큰에서 사용자 역할을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 역할
     */
    fun getUserRolesFromToken(token: String): Set<String> {
        return tokenService.getUserInfoFromToken(token).roles
    }
    
    companion object {
        /**
         * 기본 설정으로 TokenFacade 인스턴스를 생성합니다.
         */
        fun standard(): TokenFacade {
            return TokenFacade(JwtConfig.standard)
        }
        
        /**
         * 커스텀 설정으로 TokenFacade 인스턴스를 생성합니다.
         * 
         * @param configCustomizer JwtConfig.Builder를 커스터마이징하는 함수
         */
        fun custom(configCustomizer: JwtConfig.Builder.() -> Unit): TokenFacade {
            val builder = JwtConfig.builder()
            builder.configCustomizer()
            return TokenFacade(builder.build())
        }
    }
}