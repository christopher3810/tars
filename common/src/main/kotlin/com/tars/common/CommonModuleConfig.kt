package com.tars.common

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * 공통 모듈의 설정 클래스
 * 컴포넌트 스캔을 활성화합니다.
 */
@Configuration
@ComponentScan(basePackages = ["com.tars.common"])
class CommonModuleConfig 