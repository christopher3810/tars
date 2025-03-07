package com.tars.app.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

interface CoroutineDispatcherProvider {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}

class DefaultDispatcherProvider : CoroutineDispatcherProvider {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main
}

@Configuration
class CoroutineConfig {
    @Bean
    fun coroutineDispatcherProvider(): CoroutineDispatcherProvider = DefaultDispatcherProvider()
} 