package com.tars.app.config

import com.tars.auth.AuthModuleConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * 애플리케이션 모듈의 설정 클래스
 * 인증 모듈과 공통 모듈의 설정을 임포트합니다.
 */
@Configuration
@Import(AuthModuleConfig::class)
class AppConfig 