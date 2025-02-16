package dev.natig.gandalf.chatClient

import java.util.UUID
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.InMemoryChatMemory

internal fun String?.resolveConversationIdWithMemory(chatMemory: InMemoryChatMemory): String =
    this?.takeIf { it.isNotEmpty() } ?: UUID.randomUUID().toString().also { chatMemory.add(it, emptyList()) }

internal fun ChatClient.getAdvisorResponse(
    userPromptText: String,
    systemPromptText: String,
    conversationId: String
): String? = this.prompt()
    .system(systemPromptText)
    .user(userPromptText)
    .advisors { advisor ->
        advisor.param("chat_memory_conversation_id", conversationId)
            .param("chat_memory_response_size", 20)
    }
    .call()
    .entity(ChatResponse::class.java)?.response

internal fun ChatClient.getNoAdvisorResponse(
    userPromptText: String,
    systemPromptText: String
): String? = this.prompt()
    .system(systemPromptText)
    .user(userPromptText)
    .call()
    .entity(ChatResponse::class.java)?.response

enum class LogicType {
    ADVISOR,
    NO_ADVISOR
}

data class ChatResponse(
    val conversationId: String,
    val response: String
)