package com.tars.app.application.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * 로그인 유스케이스 인터페이스
 */
interface LoginUseCase {
    /**
     * 사용자 로그인 처리
     * 
     * @param request 로그인 요청
     * @return 로그인 응답
     */
    suspend fun login(request: Request): Response
    
    /**
     * 로그인 요청
     */
    data class Request(
        val email: String,
        val password: String
    ) {
        companion object {
            fun create(
                email: String,
                password: String
            ): Request {
                require(email.isNotBlank()) { "이메일은 필수입니다." }
                require(password.isNotBlank()) { "비밀번호는 필수입니다." }

                return Request(
                    email = email.trim(),
                    password = password
                )
            }
        }
    }
    
    /**
     * 로그인 응답
     */
    data class Response(
        val userId: Long,
        val email: String,
        val accessToken: String,
        val refreshToken: String,
        val expiresIn: Long = 3600 // 기본값 1시간 (초 단위)
    )
} 