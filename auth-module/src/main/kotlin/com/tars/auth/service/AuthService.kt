package com.tars.auth.service

import com.tars.auth.domain.token.type.TokenClaim
import com.tars.auth.dto.LoginRequest
import com.tars.auth.dto.TokenRefreshRequest
import com.tars.auth.dto.TokenResponse
import com.tars.auth.exception.AuthenticationException
import com.tars.auth.port.output.UserAuthPort
import com.tars.common.error.ErrorMessage
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userAuthPort: UserAuthPort,
    private val tokenProvider: TokenProvider,
    private val passwordEncoder: PasswordEncoder
) {
    /**
     * 사용자 로그인 처리
     * 
     * @param loginRequest 로그인 요청 정보
     * @return 토큰 응답
     * @throws AuthenticationException 인증 실패 시
     */
    fun login(loginRequest: LoginRequest): TokenResponse {
        // 사용자 조회
        val user = userAuthPort.findUserByEmail(loginRequest.email)
            ?: throw AuthenticationException(ErrorMessage.INVALID_CREDENTIALS.message)
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.password, user.hashedPassword)) {
            throw AuthenticationException(ErrorMessage.INVALID_CREDENTIALS.message)
        }
        
        // 토큰 생성 (사용자 ID와 역할 정보 포함)
        // 권한 검증용 토큰 빌더 사용
        val accessToken = tokenProvider.createAuthorizationTokenBuilder(user.email)
            .withClaim(TokenClaim.USER_ID.value, user.id)
            .withClaim(TokenClaim.ROLES.value, user.roles.joinToString(","))
            .build()
            
        val refreshToken = tokenProvider.createRefreshTokenBuilder(user.email)
            .withClaim(TokenClaim.USER_ID.value, user.id)
            .build()
        
        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
    
    /**
     * 토큰 갱신 처리
     * 
     * @param tokenRefreshRequest 토큰 갱신 요청 정보
     * @return 갱신된 토큰 응답
     * @throws AuthenticationException 유효하지 않은 리프레시 토큰
     */
    fun refreshToken(tokenRefreshRequest: TokenRefreshRequest): TokenResponse {
        val refreshToken = tokenRefreshRequest.refreshToken
        
        // 리프레시 토큰 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw AuthenticationException("Invalid refresh token")
        }
        
        // 사용자 이메일 추출
        val email = tokenProvider.getUsernameFromJWT(refreshToken)
        
        // 사용자 존재 확인
        val user = userAuthPort.findUserByEmail(email)
            ?: throw AuthenticationException("User not found for the token")
        
        // 새 액세스 토큰 생성 (사용자 ID와 역할 정보 포함)
        // 권한 검증용 토큰 빌더 사용
        val newAccessToken = tokenProvider.createAuthorizationTokenBuilder(user.email)
            .withClaim(TokenClaim.USER_ID.value, user.id)
            .withClaim(TokenClaim.ROLES.value, user.roles.joinToString(","))
            .build()
        
        return TokenResponse(
            accessToken = newAccessToken,
            refreshToken = refreshToken // 기존 리프레시 토큰 유지
        )
    }
} 