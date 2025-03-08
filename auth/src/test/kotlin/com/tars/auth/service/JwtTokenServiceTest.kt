package com.tars.auth.service

import com.tars.auth.config.JwtConfig
import com.tars.auth.domain.token.builder.TokenBuilder
import com.tars.auth.domain.token.type.TokenClaim
import com.tars.auth.dto.UserTokenInfo
import com.tars.auth.exception.TokenException
import io.jsonwebtoken.impl.DefaultClaims
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.mockk.*

class JwtTokenServiceTest : DescribeSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val testData = object {
        val userId = 123L
        val email = "test@example.com"
        val roles = setOf("ROLE_USER", "ROLE_ADMIN")
        val additionalClaims = mapOf("customKey" to "customValue")
        val sampleAccessToken = "sample.access.token"
        val sampleRefreshToken = "sample.refresh.token"

        // 기본 JWT 설정
        val jwtConfig = JwtConfig.builder()
            .secret("testSecretKeyForJwtTokenServiceTestLongEnough")
            .expirationMs(3600000L)
            .refreshExpirationMs(86400000L)
            .build()

        val userInfo = UserTokenInfo(
            id = userId,
            email = email,
            roles = roles,
            additionalClaims = additionalClaims
        )

        // 기본 클레임 생성 헬퍼 메서드
        fun createDefaultClaims(includeUserId: Boolean = true, includeRoles: Boolean = true): DefaultClaims {
            return DefaultClaims().apply {
                subject = email
                if (includeUserId) {
                    this[TokenClaim.USER_ID.value] = userId.toString()
                }
                if (includeRoles) {
                    this[TokenClaim.ROLES.value] = "ROLE_USER,ROLE_ADMIN"
                }
                this["customKey"] = "customValue"
            }
        }

        // 빌더 체이닝 메서드 모킹 헬퍼
        fun setupTokenBuilderMock(
            mockBuilder: TokenBuilder = mockk(),
            returnToken: String = sampleAccessToken
        ): TokenBuilder {
            every { mockBuilder.withClaim(any(), any()) } returns mockBuilder
            every { mockBuilder.build() } returns returnToken
            return mockBuilder
        }
    }

    describe("JWT 토큰 서비스는") {
        context("토큰 생성할 때") {

            val mockTokenProvider = mockk<TokenProvider>()
            val mockAccessTokenBuilder = testData.setupTokenBuilderMock(mockk(), testData.sampleAccessToken)
            val mockRefreshTokenBuilder = testData.setupTokenBuilderMock(mockk(), testData.sampleRefreshToken)

            // 토큰 생성 메서드 모킹
            every { mockTokenProvider.createAuthorizationTokenBuilder(testData.email) } returns mockAccessTokenBuilder
            every { mockTokenProvider.createRefreshTokenBuilder(testData.email) } returns mockRefreshTokenBuilder

            val sut = JwtTokenService(mockTokenProvider, testData.jwtConfig)

            val tokenResponse by lazy { sut.generateTokens(testData.userInfo) }

            it("액세스 토큰과 리프레시 토큰을 모두 반환해야 한다") {
                tokenResponse.accessToken shouldBe testData.sampleAccessToken
                tokenResponse.refreshToken shouldBe testData.sampleRefreshToken
            }

            it("만료 시간을 초 단위로 변환하여 반환해야 한다") {
                tokenResponse.expiresIn shouldBe testData.jwtConfig.expirationMs / 1000
            }

            it("사용자 ID와 역할 정보가 각 토큰에 포함되어야 한다") {
                tokenResponse.accessToken shouldBe testData.sampleAccessToken
                tokenResponse.refreshToken shouldBe testData.sampleRefreshToken

                verify {
                    mockTokenProvider.createAuthorizationTokenBuilder(testData.email)
                    mockTokenProvider.createRefreshTokenBuilder(testData.email)
                }

                verify(atLeast = 1) { mockAccessTokenBuilder.withClaim(any(), any()) }
                verify(atLeast = 1) { mockRefreshTokenBuilder.withClaim(any(), any()) }
            }
        }

        context("토큰을 리프레시 시킬 때") {
            val mockTokenProvider = mockk<TokenProvider>()
            val mockAccessTokenBuilder = testData.setupTokenBuilderMock()

            every { mockTokenProvider.validateToken(testData.sampleRefreshToken) } returns true
            every { mockTokenProvider.getClaimsFromToken(testData.sampleRefreshToken) } returns testData.createDefaultClaims()
            every { mockTokenProvider.createAuthorizationTokenBuilder(testData.email) } returns mockAccessTokenBuilder

            val sut = JwtTokenService(mockTokenProvider, testData.jwtConfig)
            val refreshedTokenResponse by lazy { sut.refreshToken(testData.sampleRefreshToken) }

            it("새 액세스 토큰을 생성하고 기존 리프레시 토큰을 유지해야 한다") {
                refreshedTokenResponse.accessToken shouldBe testData.sampleAccessToken
                refreshedTokenResponse.refreshToken shouldBe testData.sampleRefreshToken
                refreshedTokenResponse.expiresIn shouldBe testData.jwtConfig.expirationMs / 1000

                verify {
                    mockTokenProvider.validateToken(testData.sampleRefreshToken)
                    mockTokenProvider.getClaimsFromToken(testData.sampleRefreshToken)
                    mockTokenProvider.createAuthorizationTokenBuilder(testData.email)
                }
            }
        }

        context("유효하지 않은 리프레시 토큰 처리할 때") {
            val invalidRefreshToken = "invalid.refresh.token"
            val mockTokenProvider = mockk<TokenProvider> {
                every { validateToken(invalidRefreshToken) } returns false
            }

            val sut = JwtTokenService(mockTokenProvider, testData.jwtConfig)

            it("TokenException이 발생해야 한다") {
                shouldThrow<TokenException> {
                    sut.refreshToken(invalidRefreshToken)
                }

                verify { mockTokenProvider.validateToken(invalidRefreshToken) }
                verify(exactly = 0) { mockTokenProvider.getClaimsFromToken(any()) }
            }
        }

        context("토큰 검증할 때") {
            data class TokenTestCase(
                val token: String,
                val isValid: Boolean,
                val description: String
            )

            val testCases = listOf(
                TokenTestCase("valid.token", true, "유효한 토큰"),
                TokenTestCase("invalid.token", false, "유효하지 않은 토큰")
            )

            testCases.forAll { testCase ->
                val mockTokenProvider = mockk<TokenProvider> {
                    every { validateToken(testCase.token) } returns testCase.isValid
                }

                val sut = JwtTokenService(mockTokenProvider, testData.jwtConfig)

                it("${testCase.description}은 ${testCase.isValid}를 반환해야 한다") {
                    sut.validateToken(testCase.token) shouldBe testCase.isValid
                    verify { mockTokenProvider.validateToken(testCase.token) }
                }
            }
        }

        context("토큰에서 사용자 정보 추출할 때") {
            val token = "test.token"
            val mockTokenProvider = mockk<TokenProvider> {
                every { getClaimsFromToken(token) } returns testData.createDefaultClaims()
            }

            val sut = JwtTokenService(mockTokenProvider, testData.jwtConfig)
            val extractedUserInfo by lazy { sut.getUserInfoFromToken(token) }

            it("이메일, 사용자 ID, 역할 정보, 추가 클레임이 올바르게 추출되어야 한다") {
                extractedUserInfo.email shouldBe testData.email
                extractedUserInfo.id shouldBe testData.userId
                extractedUserInfo.roles shouldBe setOf("ROLE_USER", "ROLE_ADMIN")
                extractedUserInfo.additionalClaims["customKey"] shouldBe "customValue"
            }
        }

        context("사용자 ID가 없는 토큰 처리할 때") {
            val tokenWithoutUserId = "token.without.userid"
            val mockTokenProvider = mockk<TokenProvider> {
                every { getClaimsFromToken(tokenWithoutUserId) } returns testData.createDefaultClaims(includeUserId = false)
            }

            val sut = JwtTokenService(mockTokenProvider, testData.jwtConfig)

            it("TokenException이 발생해야 한다") {
                val exception = shouldThrow<TokenException> {
                    sut.getUserInfoFromToken(tokenWithoutUserId)
                }

                exception.message shouldBe "User ID not found in token"
            }
        }

        context("역할정보가 없는 토큰 일 때") {
            val tokenWithoutRoles = "token.without.roles"
            val mockTokenProvider = mockk<TokenProvider>()
            val mockAccessTokenBuilder = testData.setupTokenBuilderMock()

            every { mockTokenProvider.getClaimsFromToken(tokenWithoutRoles) } returns testData.createDefaultClaims(includeRoles = false)
            every { mockTokenProvider.validateToken(tokenWithoutRoles) } returns true
            every { mockTokenProvider.createAuthorizationTokenBuilder(testData.email) } returns mockAccessTokenBuilder

            val sut = JwtTokenService(mockTokenProvider, testData.jwtConfig)

            it("empty 역할 세트를 반환하고, 리프레시시 ROLES 클레임이 추가되지 않아야 한다") {
                val userInfo = sut.getUserInfoFromToken(tokenWithoutRoles)
                userInfo.roles shouldBe emptySet()

                val refreshResult = sut.refreshToken(tokenWithoutRoles)

                refreshResult.accessToken shouldBe testData.sampleAccessToken

                verify {
                    mockTokenProvider.validateToken(tokenWithoutRoles)
                    mockTokenProvider.getClaimsFromToken(tokenWithoutRoles)
                    mockTokenProvider.createAuthorizationTokenBuilder(testData.email)
                }
            }
        }
    }
})