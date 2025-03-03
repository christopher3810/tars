package com.tars.app.adaptor.`in`.web.config.Security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date

@Component
class TokenProvider (
    private val jwtConfig: JwtConfig
){
    // 문자열을 바이트 배열로 변환하여 적절한 Key 인스턴스 생성
    private val key: Key = Keys.hmacShaKeyFor(jwtConfig.secret.toByteArray(Charsets.UTF_8))

    fun generateToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtConfig.expirationMs)
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun generateRefreshToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtConfig.refreshExpirationMs)
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun getUsernameFromJWT(token: String): String {
        val claims: Claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject
    }

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