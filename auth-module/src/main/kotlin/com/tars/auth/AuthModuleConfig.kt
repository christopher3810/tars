package com.tars.auth

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.context.annotation.Bean

/**
 * 인증 모듈의 설정 클래스
 * 컴포넌트 스캔 및 설정 프로퍼티 스캔을 활성화합니다.
 * 
 * 이 모듈은 컨트롤러나 엔드포인트를 제공하지 않으며,
 * 다른 모듈에서 사용할 수 있는 인증/인가 관련 도구를 제공합니다.
 * 
 * 포트-어댑터 아키텍처를 사용하여 구현되었으며,
 * 클라이언트는 AuthServicePort 인터페이스를 통해 인증 기능을 사용할 수 있습니다.
 */
@Configuration
@ComponentScan(basePackages = ["com.tars.auth"])
@ConfigurationPropertiesScan(basePackages = ["com.tars.auth"])
@PropertySources(
    PropertySource("classpath:auth-module-defaults.yml", ignoreResourceNotFound = true)
)
class AuthModuleConfig {
    
    /**
     * YAML 파일을 처리하기 위한 PropertySourcesPlaceholderConfigurer 빈 등록
     */
    @Bean
    fun propertySourcesPlaceholderConfigurer(): PropertySourcesPlaceholderConfigurer {
        return PropertySourcesPlaceholderConfigurer()
    }
} 