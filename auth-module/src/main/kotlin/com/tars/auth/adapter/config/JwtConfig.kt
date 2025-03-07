package com.tars.auth.adapter.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources

@Configuration
@PropertySources(
    PropertySource("classpath:auth-module-defaults.yml", ignoreResourceNotFound = true),
    PropertySource("classpath:application.yml", ignoreResourceNotFound = true)
)
@ConfigurationProperties(prefix = "jwt")
@Qualifier("authModuleJwtConfig")
class JwtConfig {
    var secret: String = "defaultSecretKeyForJwtAuthenticationPleaseOverrideInProduction"
    var expirationMs: Long = 3600000L
    var refreshExpirationMs: Long = 86400000L
} 