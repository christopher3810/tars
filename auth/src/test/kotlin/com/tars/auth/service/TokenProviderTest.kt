package com.tars.auth.service

import com.tars.auth.config.JwtConfig
import com.tars.auth.domain.token.type.TokenPurpose
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.security.Key
import java.time.Instant
import java.util.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class TokenProviderTest : DescribeSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    // 테스트 데이터 정의
    val email = "test@example.com"
    val userId = 123L
    val roles = "ROLE_USER,ROLE_ADMIN"
    
    // 테스트용 JWT 설정
    val standardConfig = JwtConfig.builder()
        .secret("testSecretKeyForTokenProviderTestLongEnough")
        .expirationMs(1.hours.inWholeMilliseconds)
        .refreshExpirationMs(24.hours.inWholeMilliseconds)
        .build()
    
    // 테스트용 키
    val hmacKey by lazy {
        Keys.hmacShaKeyFor(standardConfig.secret.toByteArray())
    }
    
    // 테스트용 토큰 생성 헬퍼 함수
    fun createTestToken(
        subject: String = email,
        expiresAt: Instant = Instant.now() + 1.hours.toJavaDuration(),
        claims: Map<String, Any> = emptyMap(),
        key: Key = hmacKey
    ): String = Jwts.builder()
        .setSubject(subject)
        .setIssuedAt(Date.from(Instant.now()))
        .setExpiration(Date.from(expiresAt))
        .apply { claims.forEach { (name, value) -> claim(name, value) } }
        .signWith(key)
        .compact()
    
    describe("토큰 프로바이더는") {

        val sut by lazy { TokenProvider(standardConfig) }

        context("토큰 생성 요청이 들어올 때") {

            it("액세스 토큰을 적절한 파라미터로 생성해야 한다") {
                val result = sut.generateToken(email)
                
                assertSoftly {
                    // JWT 형식 검증
                    result.split(".").size shouldBe 3
                    
                    // 토큰 내용 검증
                    val claims = sut.getClaimsFromToken(result)
                    claims.subject shouldBe email
                    claims["type"] shouldBe "access"
                    
                    // 만료 시간 검증
                    val now = Instant.now()
                    claims.expiration.toInstant().isAfter(now) shouldBe true
                }
            }
            
            it("리프레시 토큰을 적절한 파라미터로 생성해야 한다") {
                val result = sut.generateRefreshToken(email)
                
                assertSoftly {
                    // JWT 형식 검증
                    result.split(".").size shouldBe 3
                    
                    // 토큰 내용 검증
                    val claims = sut.getClaimsFromToken(result)
                    claims.subject shouldBe email
                    claims["type"] shouldBe "refresh"
                    
                    // 만료 시간 검증
                    val now = Instant.now()
                    claims.expiration.toInstant().isAfter(now) shouldBe true

                }
            }
            
            it("권한 검증 토큰 빌더를 적절한 파라미터로 생성해야 한다") {
                val result = sut.createAuthorizationTokenBuilder(email)
                
                // 빌더 객체 타입 검증
                result::class.simpleName shouldBe "AuthorizationTokenBuilder"
                val token = result.build()
                
                assertSoftly {
                    // JWT 형식 검증
                    token.split(".").size shouldBe 3
                    
                    // 토큰 내용 검증
                    val claims = sut.getClaimsFromToken(token)
                    claims.subject shouldBe email
                    claims["type"] shouldBe "authorization"
                }
            }
            
            it("일회용 토큰 요청시 목적에 맞는 토큰 빌더를 생성해야 한다") {
                val purpose = TokenPurpose.EMAIL_VERIFICATION
                
                val result = sut.createOneTimeTokenBuilder(email, purpose)

                result::class.simpleName shouldBe "OneTimeTokenBuilder"

                val token = result.build()
                
                assertSoftly {
                    // JWT 형식 검증
                    token.split(".").size shouldBe 3
                    
                    // 토큰 내용 검증
                    val claims = sut.getClaimsFromToken(token)
                    claims.subject shouldBe email
                    claims["purpose"] shouldBe purpose.toString()
                    claims["type"] shouldBe "one_time"
                }
            }
        }
        
        context("토큰 검증 요청이 들어올 때") {
            it("유효한 토큰이면 검증에 성공해야 한다") {
                val token = createTestToken()
                
                val result = sut.validateToken(token)
                
                result shouldBe true
            }
        
            it("잘못된 서명의 토큰이면 검증에 실패해야 한다") {
                val validToken = createTestToken()
                val parts = validToken.split(".")
                val tamperedToken = "${parts[0]}.${parts[1]}.invalid${parts[2].substring(5)}"

                val validationResult = sut.validateToken(tamperedToken)
                
                assertSoftly {
                    validationResult shouldBe false
                    shouldThrow<SignatureException> {
                        sut.getClaimsFromToken(tamperedToken)
                    }
                }
            }
            
            it("만료된 토큰이면 검증에 실패해야 한다") {
                val expiredToken = createTestToken(
                    expiresAt = Instant.now() - 1.seconds.toJavaDuration()
                )

                val validationResult = sut.validateToken(expiredToken)
                
                assertSoftly {
                    validationResult shouldBe false
                    shouldThrow<ExpiredJwtException> {
                        sut.getClaimsFromToken(expiredToken)
                    }
                }
            }
            
            it("형식이 완전히 잘못된 토큰이면 검증에 실패해야 한다") {
                val invalidTokens = listOf(
                    "completely.invalid.token",
                    "no_dots_at_all",
                    "",
                    "just.one.dot"
                )

                invalidTokens.forEach { invalidToken ->
                    sut.validateToken(invalidToken) shouldBe false
                }
            }
        }
        
        context("토큰에서 정보를 추출할 때") {
            it("모든 클레임을 정확하게 가져와야 한다") {
                val customClaims = mapOf(
                    "userId" to userId.toString(),
                    "roles" to roles,
                    "custom" to "value"
                )
                
                val token = createTestToken(claims = customClaims)
                val claims = sut.getClaimsFromToken(token)

                assertSoftly(claims) {
                    subject shouldBe email
                    get("userId") shouldBe userId.toString()
                    get("roles") shouldBe roles
                    get("custom") shouldBe "value"
                    id shouldBe null
                    issuedAt.shouldNotBeNull()
                    expiration.shouldNotBeNull()
                }
            }
            
            it("사용자 이메일(subject)을 정확하게 가져와야 한다") {
                val token = createTestToken()
                
                val extractedEmail = sut.getUsernameFromJWT(token)
                
                extractedEmail shouldBe email
            }
        }
    }
})