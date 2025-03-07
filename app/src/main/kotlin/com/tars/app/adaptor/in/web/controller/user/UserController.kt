package com.tars.app.adaptor.`in`.web.controller.user

import com.tars.app.adaptor.`in`.web.controller.user.dto.UserRegistrationDto
import com.tars.app.application.user.UserRegistrationUseCase
import com.tars.app.application.user.service.UserRegistrationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("v1/user")
class UserController(
    private val userRegistrationService: UserRegistrationService
) {
    @Operation(
        summary = "사용자 등록",
        description = "신규 사용자를 등록합니다.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "등록 성공",
                content = [Content(schema = Schema(implementation = UserRegistrationUseCase.Response::class))]
            ),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이메일")
        ]
    )
    @PostMapping
    suspend fun registerUser(
        @Valid @RequestBody req: UserRegistrationDto
    ): ResponseEntity<UserRegistrationUseCase.Response> {
        val response = userRegistrationService.registerUser(req)
        return ResponseEntity.ok(response)
    }
}