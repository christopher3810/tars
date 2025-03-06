package com.tars.auth.controller

import com.tars.auth.dto.LoginRequest
import com.tars.auth.dto.TokenRefreshRequest
import com.tars.auth.dto.TokenResponse
import com.tars.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    /**
     * 사용자 로그인 처리
     * 
     * @param loginRequest 로그인 요청 정보
     * @return 토큰 응답
     */
    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<TokenResponse> {
        val tokenResponse = authService.login(loginRequest)
        return ResponseEntity.ok(tokenResponse)
    }
    
    /**
     * 토큰 갱신 처리
     * 
     * @param tokenRefreshRequest 토큰 갱신 요청 정보
     * @return 갱신된 토큰 응답
     */
    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody tokenRefreshRequest: TokenRefreshRequest): ResponseEntity<TokenResponse> {
        val tokenResponse = authService.refreshToken(tokenRefreshRequest)
        return ResponseEntity.ok(tokenResponse)
    }
} 