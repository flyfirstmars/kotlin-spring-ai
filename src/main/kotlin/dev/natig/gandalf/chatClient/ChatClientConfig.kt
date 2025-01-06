package dev.natig.gandalf.chatClient

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChatClientConfig() {

    @Bean
    fun chatMemory(): InMemoryChatMemory = InMemoryChatMemory()

    @Bean
    fun chatClient(builder: ChatClient.Builder, chatMemory: InMemoryChatMemory): ChatClient = builder
        .defaultAdvisors(MessageChatMemoryAdvisor(chatMemory))
        .build()

    @Bean
    fun noAdvisorChatClient(builder: ChatClient.Builder): ChatClient = builder.build()
}