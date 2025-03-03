package com.tars.app.adaptor.`in`.web.controller.auth

import com.tars.app.adaptor.`in`.web.config.Security.TokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

//TODO : 걍 api gateway 만들까..
@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val tokenProvider: TokenProvider
) {

    //TODO : username 말고 ssn 으로 변경할것
    @PostMapping("/login")
    fun login(@RequestParam username: String): ResponseEntity<Map<String, String>> {
        val accessToken = tokenProvider.generateToken(username)
        val refreshToken = tokenProvider.generateRefreshToken(username)
        return ResponseEntity.ok(mapOf("accessToken" to accessToken, "refreshToken" to refreshToken))
    }
}
