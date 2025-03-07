package com.tars.app.adaptor.`in`.security.config

import com.tars.app.adaptor.`in`.auth.TokenServiceAdapter
import com.tars.app.adaptor.`in`.security.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Spring Security 6+ 설정 클래스
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val tokenServiceAdapter: TokenServiceAdapter
) {

    /**
     * Security Filter Chain 설정
     */
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // CSRF 보호 비활성화 (REST API에서는 필요 없음)
            .csrf { it.disable() }
            // CORS 설정 활성화
            .cors { it.configurationSource(corsConfigurationSource()) }
            // 세션 관리 설정 (STATELESS: 세션 사용 안함)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            // 요청 경로별 권한 설정
            .authorizeHttpRequests { authz ->
                authz
                    // 인증 없이 접근 가능한 경로
                    .requestMatchers(
                        "/api/v1/auth/login",
                        "/api/v1/auth/signup",
                        "/api/v1/auth/refresh",
                        "/h2-console/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/actuator/**"
                    ).permitAll()
                    // 관리자 권한이 필요한 경로
                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                    // 그 외 모든 요청은 인증 필요
                    .anyRequest().authenticated()
            }
            // H2 콘솔 사용 시 필요한 설정
            .headers { 
                it.frameOptions { frame -> frame.sameOrigin() } 
            }
            // JWT 필터 추가 (UsernamePasswordAuthenticationFilter 이전에 실행)
            .addFilterBefore(
                JwtAuthenticationFilter(tokenServiceAdapter),
                UsernamePasswordAuthenticationFilter::class.java
            )
        
        return http.build()
    }

    /**
     * CORS 설정
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*") // 모든 출처 허용 (프로덕션에서는 구체적인 도메인으로 변경)
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.maxAge = 3600L
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
    
    /**
     * 비밀번호 인코더 빈 등록
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
} 