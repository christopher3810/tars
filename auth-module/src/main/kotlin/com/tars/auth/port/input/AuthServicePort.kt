package com.tars.auth.port.input

import com.tars.auth.dto.TokenResponse

/**
 * 인증 서비스 입력 포트 인터페이스
 * 외부 모듈에서 인증 기능을 사용하기 위한 인터페이스입니다.
 */
interface AuthServicePort {
    /**
     * 사용자 로그인 처리
     * 
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     * @return 토큰 응답
     */
    fun login(email: String, password: String): TokenResponse
    
    /**
     * 토큰 갱신 처리
     * 
     * @param refreshToken 리프레시 토큰
     * @return 갱신된 토큰 응답
     */
    fun refreshToken(refreshToken: String): TokenResponse
    
    /**
     * 토큰 검증
     * 
     * @param token 검증할 토큰
     * @return 토큰이 유효하면 true, 아니면 false
     */
    fun validateToken(token: String): Boolean
    
    /**
     * 토큰에서 사용자 이메일 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    fun getUserEmailFromToken(token: String): String
} 