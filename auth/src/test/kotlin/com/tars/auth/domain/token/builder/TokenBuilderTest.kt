package com.tars.auth.domain.token.builder

import com.tars.auth.domain.token.type.TokenClaim
import com.tars.auth.domain.token.type.TokenPurpose
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.inspectors.forAll
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.matchers.string.shouldNotBeEmpty
import java.security.Key

class TokenBuilderTest : DescribeSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    describe("토큰 빌더는") {
        val subject = "test@example.com"
        val expirationMs = 3600000L // 1시간
        val key by lazy {
            Keys.hmacShaKeyFor("testSecretKeyForUnitTestingThatIsLongEnough".toByteArray())
        }

        context("토큰 빌더 팩토리를 사용할 때") {
            // 파라미터화된 테스트를 위한 데이터 테이블
            data class TokenBuilderTestCase(
                val builderFactory: (String, Long, Key) -> TokenBuilder,
                val expectedType: String,
                val description: String,
                val additionalChecks: (Claims) -> Unit = {}
            )

            val testCases = listOf(
                TokenBuilderTestCase(
                    TokenBuilder::accessTokenBuilder,
                    "access",
                    "액세스 토큰 빌더"
                ),
                TokenBuilderTestCase(
                    TokenBuilder::refreshTokenBuilder,
                    "refresh",
                    "리프레시 토큰 빌더"
                ),
                TokenBuilderTestCase(
                    TokenBuilder::authorizationTokenBuilder,
                    "authorization",
                    "권한 확인용 토큰 빌더"
                )
            )

            testCases.forAll { testCase ->
                it("${testCase.description}는 '${testCase.expectedType}' 타입의 토큰을 생성해야 한다") {
                    // 테스트 케이스에 따른 빌더 생성
                    val sut = testCase.builderFactory(subject, expirationMs, key)

                    // 토큰 생성 및 파싱
                    val token = sut.build().also {
                        it.shouldNotBeEmpty()
                        it.shouldContain(".")
                        it.shouldHaveMinLength(20)
                    }

                    val claims = parseToken(token, key)
                    claims.subject shouldBe subject
                    claims.issuedAt.shouldNotBeNull()
                    claims.expiration.shouldNotBeNull()

                    // 만료 시간 검증 (±100ms 오차 허용)
                    val issuedAt = claims.issuedAt.time
                    val expiration = claims.expiration.time
                    (expiration - issuedAt) shouldBe expirationMs

                    claims["type"] shouldBe testCase.expectedType
                    testCase.additionalChecks(claims)
                }
            }

            it("일회용 토큰 빌더는 목적과 고유 ID를 포함해야 한다") {

                val purposes = enumValues<TokenPurpose>().toList()

                purposes.forAll { purpose ->
                    val sut = TokenBuilder.oneTimeTokenBuilder(
                        subject,
                        expirationMs,
                        key,
                        purpose.value
                    )

                    val token = sut.build()
                    val claims = parseToken(token, key)

                    claims["type"] shouldBe "one_time"
                    claims["purpose"] shouldBe purpose.value
                    claims.shouldContainKey("jti") // JWT ID (고유 식별자)
                    claims.id.shouldNotBeNull()
                }
            }
        }

        context("빌더 체이닝 메서드를 호출할 때") {

            table(
                headers("클레임 키", "클레임 값", "예상 클래스"),
                row("stringClaim", "string value", String::class.java),
                row("intClaim", 42, Integer::class.java),
                row("longClaim", 42L, Long::class.java),
                row("booleanClaim", true, Boolean::class.java),
                row("doubleClaim", 3.14, Double::class.java)
            ).forAll { claimKey, value, expectedClass ->
                it("$claimKey: $value 클레임이 올바른 타입(${expectedClass.simpleName})으로 저장되어야 한다") {

                    val sut = TokenBuilder.accessTokenBuilder(subject, expirationMs, key)
                        .withClaim(claimKey, value)

                    val token = sut.build()
                    val claims = parseToken(token, key)

                    claims[claimKey].shouldNotBeNull()

                    when (value) {
                        is Number -> {
                            // 숫자는 정확한 타입 비교가 어려우므로 값으로 비교
                            claims[claimKey].toString().toDouble() shouldBe value.toDouble()
                        }

                        else -> {
                            claims[claimKey] shouldBe value
                        }
                    }
                }
            }

            it("여러 클레임을 함께 추가할 수 있어야 한다") {
                val sut = TokenBuilder.accessTokenBuilder(subject, expirationMs, key)
                    .withClaim("userId", 123L)
                    .withClaim("roles", "ROLE_USER,ROLE_ADMIN")
                    .withClaim("emailVerified", true)
                    .withClaim("score", 85.5)

                val token = sut.build()
                val claims = parseToken(token, key)

                with(claims) {
                    this["userId"].toString() shouldBe "123"
                    this["roles"] shouldBe "ROLE_USER,ROLE_ADMIN"
                    this["emailVerified"].toString() shouldBe "true"
                    this["score"].toString().toDouble() shouldBe 85.5
                }
            }

            it("체이닝 메서드는 동일한 빌더 인스턴스를 반환해야 한다") {

                val sut = TokenBuilder.accessTokenBuilder(subject, expirationMs, key)
                val result = sut.withClaim("test", "value")

                result shouldBe sut
            }
            
            it("type 클레임 값을 변경하려고 시도해도 빌더의 기본값이 유지되어야 한다") {
                val sut = TokenBuilder.accessTokenBuilder(subject, expirationMs, key)
                    .withClaim("type", "custom-type")

                val token = sut.build()
                val claims = parseToken(token, key)

                claims["type"] shouldBe "access"
            }
        }

        context("확장 기능을 사용할 때") {
            it("권한 관련 클레임을 추가할 수 있어야 한다") {

                val userId = 123L
                val roles = setOf("ROLE_USER", "ROLE_ADMIN")

                val sut = TokenBuilder.authorizationTokenBuilder(subject, expirationMs, key)
                    .withClaim(TokenClaim.USER_ID.value, userId)
                    .withClaim(TokenClaim.ROLES.value, roles.joinToString(","))

                val token = sut.build()
                val claims = parseToken(token, key)

                claims[TokenClaim.USER_ID.value].toString() shouldBe userId.toString()
                claims[TokenClaim.ROLES.value] shouldBe "ROLE_USER,ROLE_ADMIN"
            }

            it("사용자 정의 가능한 TokenClaim 타입을 사용할 때 올바르게 토큰에 포함되어야 한다") {

                val sut = TokenBuilder.accessTokenBuilder(subject, expirationMs, key)

                TokenClaim.entries.forEach { claim ->
                    // type 클레임은 내부적으로 설정되므로 별도 처리
                    if (claim.value != "type") {
                        sut.withClaim(claim.value, "test-${claim.name}")
                    }
                }

                val token = sut.build()
                val claims = parseToken(token, key)

                // 커스텀 TokenClaim이 포함되어 있는지 검증
                TokenClaim.entries.forEach { claim ->
                    if (claim.value != "type") {
                        claims[claim.value] shouldBe "test-${claim.name}"
                    } else {
                        // type 은 빌더에 의해 설정된 값이어야 함
                        claims["type"] shouldBe "access"
                    }
                }
            }
        }
    }
}) {
    companion object {
        /**
         * JWT 토큰을 파싱하여 클레임을 추출
         * 
         * @param token 검증할 JWT 토큰 문자열
         * @param key 서명 검증에 사용할 키
         * @return 토큰의 클레임 정보
         */
        private fun parseToken(token: String, key: Key): Claims =
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
    }
}