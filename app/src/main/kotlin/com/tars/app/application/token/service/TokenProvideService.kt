package com.tars.app.application.token.service

import com.tars.app.application.token.TokenProvideUseCase
import com.tars.app.domain.user.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenProvideService: TokenProvideUseCase {
    private val secretKey = "CHANGE_IN_PRODUCTION"
    private val accessTokenValidityMs = 60 * 60 * 1000L // 1 hour

    override fun generateAccessToken(userDomain: User): String {

            val now = Date()
            val expiry = Date(now.time + accessTokenValidityMs)

            val claims = Jwts.claims().setSubject(userDomain.id.toString())
            claims["email"] = userDomain.email
            claims["roles"] = userDomain.roles.toList()

            return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact()

    }

    fun validateToken(token: String): Boolean = try {
        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
        !claims.body.expiration.before(Date())
    } catch (e: Exception) {
        false
    }

    fun getUserIdFromToken(token: String): Long? = try {
        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
        claims.body.subject.toLong()
    } catch (e: Exception) {
        null
    }
}