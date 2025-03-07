package com.tars.app.adaptor.`in`.web.controller.user

import com.tars.app.adaptor.`in`.auth.TokenServiceAdapter
import com.tars.app.adaptor.`in`.web.controller.user.dto.auth.LoginRequest
import com.tars.app.adaptor.`in`.web.controller.user.dto.auth.RefreshTokenRequest
import com.tars.app.adaptor.`in`.web.controller.user.dto.auth.ValidateTokenRequest
import com.tars.app.application.user.LoginUseCase
import com.tars.auth.dto.TokenResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 인증 관련 컨트롤러
 */
@RestController
@RequestMapping("v1/auth")
@Tag(name = "인증 API", description = "로그인 및 토큰 관련 API")
class AuthController(
    private val loginUseCase: LoginUseCase,
    private val tokenServiceAdapter: TokenServiceAdapter
) {
    /**
     * 로그인 API
     */
    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호로 로그인합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = [Content(schema = Schema(implementation = LoginUseCase.Response::class))]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @PostMapping("/login")
    suspend fun login(
        @Valid @RequestBody req: LoginRequest
    ): ResponseEntity<LoginUseCase.Response> {
        // 컨트롤러 DTO를 유스케이스 Request로 변환
        val request = LoginUseCase.Request(
            email = req.email,
            password = req.password
        )
        
        val response = loginUseCase.login(request)
        return ResponseEntity.ok(response)
    }

    /**
     * 토큰 갱신 API
     */
    @Operation(
        summary = "토큰 갱신",
        description = "리프레시 토큰을 사용하여 액세스 토큰을 갱신합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "토큰 갱신 성공",
                content = [Content(schema = Schema(implementation = TokenResponse::class))]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
        ]
    )
    @PostMapping("/refresh")
    suspend fun refreshToken(
        @Valid @RequestBody req: RefreshTokenRequest
    ): ResponseEntity<TokenResponse> {
        val response = tokenServiceAdapter.refreshToken(req.refreshToken)
        return ResponseEntity.ok(response)
    }

    /**
     * 토큰 검증 API
     */
    @Operation(
        summary = "토큰 검증",
        description = "액세스 토큰의 유효성을 검증합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "유효한 토큰"),
            ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
        ]
    )
    @PostMapping("/validate")
    suspend fun validateToken(
        @Valid @RequestBody req: ValidateTokenRequest
    ): ResponseEntity<Map<String, Boolean>> {
        val isValid = tokenServiceAdapter.validateToken(req.token)
        return ResponseEntity.ok(mapOf("valid" to isValid))
    }
} 