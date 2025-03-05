package com.tars.app.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.context.annotation.Bean
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig {
    
    @Bean(name = ["eventTaskExecutor"])
    fun eventTaskExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2
        executor.maxPoolSize = 5
        executor.queueCapacity = 10
        executor.setThreadNamePrefix("EventAsync-")
        executor.initialize()
        return executor
    }
} 