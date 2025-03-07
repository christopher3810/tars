package com.tars.app.adaptor.`in`.security.filter

import com.tars.app.adaptor.`in`.auth.TokenServiceAdapter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * JWT 인증 필터
 * 
 * 모든 HTTP 요청에 대해 JWT 토큰을 검증하고, 인증 정보를 SecurityContext 에 설정.
 */
@Component
class JwtAuthenticationFilter(
    private val tokenServiceAdapter: TokenServiceAdapter
) : OncePerRequestFilter() {
    
    private val log = LoggerFactory.getLogger(this::class.java)
    
    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
    
    override fun doFilterInternal(
        request: HttpServletRequest, 
        response: HttpServletResponse, 
        filterChain: FilterChain
    ) {
        try {
            // 헤더에서 JWT 토큰 추출
            val jwtToken = extractJwtFromRequest(request)
            
            // 토큰이 유효하면 인증 정보 설정
            if (jwtToken != null && tokenServiceAdapter.validateToken(jwtToken)) {
                // 토큰에서 사용자 정보 추출
                val email = tokenServiceAdapter.getUserEmailFromToken(jwtToken)
                val userId = tokenServiceAdapter.getUserIdFromToken(jwtToken)
                
                // 사용자 권한 설정 (필요 시 토큰에서 추출하거나 DB에서 조회)
                val authorities = extractAuthoritiesFromToken(jwtToken)
                
                // SecurityContext 에 인증 정보 설정
                val authentication = UsernamePasswordAuthenticationToken(
                    email,  // principal
                    null,   // credentials (비밀번호는 필요 없음)
                    authorities
                )
                
                // 추가 정보 설정 (필요시 사용)
                val details = mutableMapOf<String, Any>()
                details["userId"] = userId
                authentication.details = details
                
                SecurityContextHolder.getContext().authentication = authentication
                
                log.debug("인증 완료: $email")
            }
        } catch (e: Exception) {
            log.error("JWT 인증 처리 중 오류 발생", e)
            // 인증 오류가 발생해도 필터 체인은 계속 진행
            // SecurityContext에 인증 정보가 없으면 Spring Security가 인증 실패로 처리
        }
        
        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response)
    }
    
    /**
     * HTTP 요청 헤더에서 JWT 토큰을 추출합니다.
     */
    private fun extractJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        
        return if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else {
            null
        }
    }
    
    /**
     * JWT 토큰에서 권한 정보를 추출합니다.
     * 실제 구현에서는 토큰에 저장된 권한 정보를 파싱하거나, 필요시 DB 조회를 통해 권한을 조회할 수 있습니다.
     */
    private fun extractAuthoritiesFromToken(token: String): Set<SimpleGrantedAuthority> {
        // 토큰에서 권한 정보를 추출하는 로직 구현
        // 예시 구현입니다. 실제로는 tokenServiceAdapter 를 통해 권한 정보를 가져와야 합니다.
        return try {
            // 여기서는 실제 TokenServiceAdapter 에서 권한 정보를 제공한다고 가정합니다.
            // 권한 정보를 SimpleGrantedAuthority 로 변환
            setOf(SimpleGrantedAuthority("ROLE_USER"))
        } catch (e: Exception) {
            log.error("토큰에서 권한 정보 추출 실패", e)
            emptySet()
        }
    }
} 