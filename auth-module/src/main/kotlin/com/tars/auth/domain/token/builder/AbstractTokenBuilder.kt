package com.tars.auth.domain.token.builder

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.security.Key
import java.util.Date

/**
 * TokenBuilder 인터페이스의 기본 구현을 제공하는 추상 클래스
 * 모든 토큰 빌더 구현체의 공통 로직을 포함합니다.
 */
abstract class AbstractTokenBuilder(
    protected val subject: String,
    protected val expirationMs: Long,
    protected val key: Key
) : TokenBuilder {
    protected val claims: MutableMap<String, Any> = mutableMapOf()
    protected var issuedAt: Date = Date()
    
    override fun withClaim(key: String, value: Any): TokenBuilder {
        claims[key] = value
        return this
    }
    
    override fun withClaims(claims: Map<String, Any>): TokenBuilder {
        this.claims.putAll(claims)
        return this
    }
    
    override fun withIssuedAt(issuedAt: Date): TokenBuilder {
        this.issuedAt = issuedAt
        return this
    }
    
    override fun build(): String {
        val now = issuedAt
        val expiryDate = Date(now.time + expirationMs)
        
        val builder = Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
        
        // 추가 클레임 설정
        addClaims(builder)
        
        // 구현체별 추가 설정
        customizeBuild(builder)
        
        return builder.signWith(key, SignatureAlgorithm.HS256).compact()
    }
    
    /**
     * 기본 클레임을 JWT 빌더에 추가합니다.
     * 
     * @param builder JWT 빌더
     */
    protected open fun addClaims(builder: io.jsonwebtoken.JwtBuilder) {
        claims.forEach { (key, value) ->
            builder.claim(key, value)
        }
    }
    
    /**
     * 구현체별 추가 설정을 적용합니다.
     * 하위 클래스에서 오버라이드하여 토큰 생성 전략별 커스터마이징을 구현합니다.
     * 
     * @param builder JWT 빌더
     */
    protected open fun customizeBuild(builder: io.jsonwebtoken.JwtBuilder) {
        // 기본 구현은 아무 작업도 수행하지 않음
    }
} 