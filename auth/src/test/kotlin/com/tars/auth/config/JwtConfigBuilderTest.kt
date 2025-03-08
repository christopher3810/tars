package com.tars.auth.config

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class JwtConfigBuilderTest : DescribeSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    describe("JwtConfigBuilder 클래스는") {
        context("기본 설정값으로 생성될 때") {
            val builder = JwtConfigBuilder()
            val config = builder.build()

            it("StandardJwtStrategy 의 기본값들을 가져야 한다") {
                config.secret shouldBe StandardJwtStrategy.secret
                config.expirationMs shouldBe StandardJwtStrategy.expirationMs
                config.refreshExpirationMs shouldBe StandardJwtStrategy.refreshExpirationMs
            }
        }

        context("커스텀 설정값으로 빌드할 때") {
            val customSecret = "customSecret"
            val customExpirationMs = 1000L
            val customRefreshExpirationMs = 2000L

            val config = JwtConfigBuilder()
                .secret(customSecret)
                .expirationMs(customExpirationMs)
                .refreshExpirationMs(customRefreshExpirationMs)
                .build()

            it("설정된 커스텀 값들을 가져야 한다") {
                config.secret shouldBe customSecret
                config.expirationMs shouldBe customExpirationMs
                config.refreshExpirationMs shouldBe customRefreshExpirationMs
            }
        }

        context("메서드 체이닝으로 설정할 때") {
            it("빌더 인스턴스를 반환하여 체이닝이 가능해야 한다") {
                val builder = JwtConfigBuilder()
                val result1 = builder.secret("test")
                val result2 = result1.expirationMs(1000L)
                val result3 = result2.refreshExpirationMs(2000L)

                result1 shouldBe builder
                result2 shouldBe builder
                result3 shouldBe builder
            }
        }
    }
})