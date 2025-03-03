package com.tars.app.adaptor.`in`.web.config.Security.filter

import org.springframework.stereotype.Component

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider
){
}