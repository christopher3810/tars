package com.tars.auth

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * 인증 모듈의 설정 클래스
 * 컴포넌트 스캔 및 설정 프로퍼티 스캔을 활성화합니다.
 */
@Configuration
@ComponentScan(basePackages = ["com.tars.auth"])
@ConfigurationPropertiesScan(basePackages = ["com.tars.auth"])
class AuthModuleConfig 