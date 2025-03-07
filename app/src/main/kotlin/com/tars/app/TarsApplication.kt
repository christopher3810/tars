package com.tars.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

/**
 * 애플리케이션의 진입점
 */
@SpringBootApplication(scanBasePackages = ["com.tars.app"])
@EnableFeignClients
class AppApplication

fun main(args: Array<String>) {
	runApplication<AppApplication>(*args)
}
