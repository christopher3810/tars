package com.tars.app

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * 애플리케이션 컨텍스트 로드 테스트
 */
@SpringBootTest
@ActiveProfiles("test") // 테스트 프로필 활성화
class AppApplicationTests {

	@Test
	fun contextLoads() {
		// 애플리케이션 컨텍스트가 정상적으로 로드되는지 확인
	}
}
