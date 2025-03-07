package com.tars.app

import com.tars.auth.AuthModuleConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Import

/**
 * 애플리케이션의 진입점
 * auth-module의 설정을 가져와서 통합합니다.
 */
@SpringBootApplication(scanBasePackages = ["com.tars.app"])
@EnableFeignClients
@Import(AuthModuleConfig::class)
class AppApplication

fun main(args: Array<String>) {
	runApplication<AppApplication>(*args)
}
