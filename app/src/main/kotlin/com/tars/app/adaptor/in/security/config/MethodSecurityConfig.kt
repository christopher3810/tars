package com.tars.app.adaptor.`in`.security.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

/**
 * 메서드 수준 보안 설정
 * 
 * @PreAuthorize, @PostAuthorize, @Secured, @RolesAllowed 등의 어노테이션을 활성화합니다.
 * 예: @PreAuthorize("hasRole('ADMIN')") fun adminMethod() { ... }
 */
@Configuration
@EnableMethodSecurity(
    prePostEnabled = true,     // @PreAuthorize, @PostAuthorize 활성화
    securedEnabled = true,     // @Secured 활성화
    jsr250Enabled = true       // @RolesAllowed 활성화
)
class MethodSecurityConfig 