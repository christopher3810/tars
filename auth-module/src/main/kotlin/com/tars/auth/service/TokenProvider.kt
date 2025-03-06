package com.tars.auth.service

import com.tars.auth.config.JwtConfig
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date

@Component
class TokenProvider(
    private val jwtConfig: JwtConfig
) {
    // 문자열을 바이트 배열로 변환하여 적절한 Key 인스턴스 생성
    private val key: Key = Keys.hmacShaKeyFor(jwtConfig.secret.toByteArray(Charsets.UTF_8))

    /**
     * 사용자의 이메일을 기반으로 JWT 토큰을 생성합니다.
     * 
     * @param email 사용자의 이메일
     * @return 생성된 JWT 토큰
     */
    fun generateToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtConfig.expirationMs)
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    /**
     * 사용자의 이메일을 기반으로 리프레시 토큰을 생성합니다.
     * 
     * @param email 사용자의 이메일
     * @return 생성된 리프레시 토큰
     */
    fun generateRefreshToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtConfig.refreshExpirationMs)
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    /**
     * JWT 토큰에서 사용자의 이메일을 추출합니다.
     * 
     * @param token JWT 토큰
     * @return 사용자의 이메일
     */
    fun getUsernameFromJWT(token: String): String {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     * 
     * @param authToken JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    fun validateToken(authToken: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken)
            true
        } catch (ex: Exception) {
            // 예외 처리: 로그 기록, 특정 예외에 따른 재처리 고려
            false
        }
    }
} 