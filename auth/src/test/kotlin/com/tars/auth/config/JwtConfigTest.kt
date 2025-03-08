package com.tars.auth.config

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class JwtConfigTest : DescribeSpec({
    
    isolationMode = IsolationMode.InstancePerLeaf
    
    describe("JwtConfig는") {
        context("표준 설정으로 생성될 때") {
            val standardConfig = JwtConfig.standard()
            
            it("표준 JWT 전략 값과 일치해야 한다") {
                standardConfig.secret shouldBe StandardJwtStrategy.secret
                standardConfig.expirationMs shouldBe StandardJwtStrategy.expirationMs
                standardConfig.refreshExpirationMs shouldBe StandardJwtStrategy.refreshExpirationMs
            }
        }
        
        context("builder()를 통해 생성될 때") {
            it("모든 값이 설정된 경우 해당 값을 반영해야 한다") {
                val customConfig = JwtConfig.builder()
                    .secret("customSecret")
                    .expirationMs(1000L)
                    .refreshExpirationMs(2000L)
                    .build()
                
                customConfig.secret shouldBe "customSecret"
                customConfig.expirationMs shouldBe 1000L
                customConfig.refreshExpirationMs shouldBe 2000L
            }
            
            it("일부 값만 설정된 경우 나머지는 기본값을 유지해야 한다") {
                val partialConfig = JwtConfig.builder()
                    .secret("customSecret")
                    .build()
                
                partialConfig.secret shouldBe "customSecret"
                partialConfig.expirationMs shouldBe StandardJwtStrategy.expirationMs
                partialConfig.refreshExpirationMs shouldBe StandardJwtStrategy.refreshExpirationMs
            }
        }
    }
}) 