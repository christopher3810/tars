package com.tars.auth

import com.tars.auth.config.JwtConfig
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty

class TokenFacadeTest : DescribeSpec({
    // 테스트 데이터
    val userId = 123L
    val email = "test@example.com"
    val roles = setOf("ROLE_USER", "ROLE_ADMIN")
    val additionalClaims = mapOf("customKey" to "customValue")

    describe("TokenFacade 는") {
        context("표준 설정 일때") {
            val facade = TokenFacade.standard()

            it("기본 설정으로 토큰을 생성하고 검증할 수 있어야 한다") {
                // 기본 토큰 생성
                val tokens = facade.generateTokens(userId, email)
                
                // 토큰 형식 검증
                tokens.accessToken.shouldNotBeEmpty()
                tokens.refreshToken.shouldNotBeEmpty()
                
                // 검증 API 테스트
                facade.validateToken(tokens.accessToken) shouldBe true
                
                // 사용자 정보 추출 검증
                val userInfo = facade.getUserInfoFromToken(tokens.accessToken)
                userInfo.id shouldBe userId
                userInfo.email shouldBe email
            }
        }

        context("커스텀 설정") {
            // 커스텀 설정으로 TokenFacade 생성
            val facade = TokenFacade.custom {
                JwtConfig.builder()
                    .secret("testSecretKeyForTokenFacadeTestLongEnough")
                    .expirationMs(3600000L)
                    .refreshExpirationMs(86400000L)
                    .build()
            }
            
            it("역할 정보 없이 생성된 토큰도 정상 처리되어야 한다") {
                // 역할 정보 없이 토큰 생성
                val tokens = facade.generateTokens(userId, email)
                
                // 역할 정보 확인
                val userInfo = facade.getUserInfoFromToken(tokens.accessToken)
                userInfo.roles shouldBe emptySet()
                
                // 역할 정보 추출 API 테스트
                facade.getUserRolesFromToken(tokens.accessToken) shouldBe emptySet()
            }

            context("토큰 생성 시") {
                it("액세스 토큰과 리프레시 토큰이 모두 생성되어야 한다") {
                    // 토큰 생성 (모든 정보 포함)
                    val tokens = facade.generateTokens(userId, email, roles, additionalClaims)
                    tokens.accessToken.shouldNotBeEmpty()
                    tokens.refreshToken.shouldNotBeEmpty()
                }
                
                it("액세스 토큰에는 사용자 정보와 역할이 포함되어야 한다") {
                    val tokens = facade.generateTokens(userId, email, roles, additionalClaims)
                    val accessTokenUserInfo = facade.getUserInfoFromToken(tokens.accessToken)
                    
                    accessTokenUserInfo.id shouldBe userId
                    accessTokenUserInfo.email shouldBe email
                    accessTokenUserInfo.roles shouldBe roles
                    accessTokenUserInfo.additionalClaims["customKey"] shouldBe additionalClaims["customKey"]
                    accessTokenUserInfo.additionalClaims["type"] shouldBe "authorization"
                }
                
                it("리프레시 토큰에는 사용자 정보가 포함되어야 한다") {
                    val tokens = facade.generateTokens(userId, email, roles, additionalClaims)
                    val refreshTokenUserInfo = facade.getUserInfoFromToken(tokens.refreshToken)
                    
                    refreshTokenUserInfo.id shouldBe userId
                    refreshTokenUserInfo.email shouldBe email
                    refreshTokenUserInfo.additionalClaims["type"] shouldBe "refresh"
                }
            }
            
            context("토큰에서 정보 추출 시") {
                it("개별 필드를 정확히 추출해야 한다") {
                    val tokens = facade.generateTokens(userId, email, roles, additionalClaims)
                    
                    facade.getUserEmailFromToken(tokens.accessToken) shouldBe email
                    facade.getUserIdFromToken(tokens.accessToken) shouldBe userId
                    facade.getUserRolesFromToken(tokens.accessToken) shouldBe roles
                }
            }
            
            context("토큰 리프레시 시") {
                it("새로운 액세스 토큰이 생성되어야 한다") {
                    val tokens = facade.generateTokens(userId, email, roles, additionalClaims)
                    val refreshedTokens = facade.refreshToken(tokens.refreshToken)
                    
                    refreshedTokens.accessToken.shouldNotBeEmpty()
                    refreshedTokens.refreshToken.shouldNotBeEmpty()
                }
                
                it("리프레시된 액세스 토큰은 원본과 동일한 사용자 정보를 포함해야 한다") {
                    val tokens = facade.generateTokens(userId, email, roles, additionalClaims)
                    val refreshedTokens = facade.refreshToken(tokens.refreshToken)
                    
                    val originalAccessClaims = facade.getUserInfoFromToken(tokens.accessToken)
                    val refreshedAccessClaims = facade.getUserInfoFromToken(refreshedTokens.accessToken)
                    
                    refreshedAccessClaims.id shouldBe originalAccessClaims.id
                    refreshedAccessClaims.email shouldBe originalAccessClaims.email
                    
                    facade.validateToken(refreshedTokens.accessToken) shouldBe true
                }
                
                it("리프레시된 토큰에서 올바른 정보를 추출할 수 있어야 한다") {
                    val tokens = facade.generateTokens(userId, email, roles, additionalClaims)
                    val refreshedTokens = facade.refreshToken(tokens.refreshToken)
                    
                    val refreshedAccessTokenUserInfo = facade.getUserInfoFromToken(refreshedTokens.accessToken)
                    refreshedAccessTokenUserInfo.id shouldBe userId
                    refreshedAccessTokenUserInfo.email shouldBe email
                    refreshedAccessTokenUserInfo.additionalClaims["type"] shouldBe "authorization"
                    
                    val refreshedRefreshTokenUserInfo = facade.getUserInfoFromToken(refreshedTokens.refreshToken)
                    refreshedRefreshTokenUserInfo.id shouldBe userId
                    refreshedRefreshTokenUserInfo.email shouldBe email
                    refreshedRefreshTokenUserInfo.additionalClaims["type"] shouldBe "refresh"
                }
            }
        }
    }
}) 
