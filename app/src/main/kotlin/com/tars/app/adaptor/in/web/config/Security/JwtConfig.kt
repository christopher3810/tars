package com.tars.app.adaptor.`in`.web.config.Security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jwt")
class JwtConfig {
    lateinit var secret: String
    var expirationMs: Long = 3600000L
    var refreshExpirationMs: Long = 86400000L
}