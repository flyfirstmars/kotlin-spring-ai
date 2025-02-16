package dev.natig.gandalf.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoroutineConfig {
    @Bean
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
}