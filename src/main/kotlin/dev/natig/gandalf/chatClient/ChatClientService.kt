package dev.natig.gandalf.chatClient

import dev.natig.gandalf.common.Prompts
import java.util.UUID
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.ai.chat.messages.Message
import org.springframework.stereotype.Service

@Service
class ChatClientService(
    private val chatClient: ChatClient,
    private val chatMemory: InMemoryChatMemory
) {

    fun getChatResponse(
        conversationId: String?,
        logicType: ChatClientController.LogicType,
        message: String
    ): ChatClientController.ChatResponse {
        return when (logicType) {
            ChatClientController.LogicType.ADVISOR -> {
                val resolvedConversationId = getOrCreateConversationId(conversationId)
                val response = getChatClientResponse(
                    message,
                    Prompts.CHAT_SYSTEM_PROMPT,
                    resolvedConversationId
                ).orEmpty()
                ChatClientController
                    .ChatResponse(
                        conversationId = resolvedConversationId,
                        response = response
                    )
            }

            ChatClientController.LogicType.NO_ADVISOR -> {
                val response = getNoAdvisorChatClientResponse(
                    message,
                    Prompts.CHAT_SYSTEM_PROMPT
                ).orEmpty()
                ChatClientController
                    .ChatResponse(
                        conversationId = "",
                        response = response
                    )
            }
        }
    }

    private fun getOrCreateConversationId(conversationId: String?): String =
        conversationId ?: UUID.randomUUID().toString().also {
            chatMemory.add(it, emptyList())
        }

    fun getChatClientResponse(
        userPromptText: String,
        systemPromptText: String,
        conversationId: String
    ): String? =
        chatClient.prompt()
            .system(systemPromptText)
            .user(userPromptText)
            .advisors { advisor ->
                advisor.param("chat_memory_conversation_id", conversationId)
                    .param("chat_memory_response_size", 20)
            }
            .call()
            .content()

    fun getNoAdvisorChatClientResponse(
        userPromptText: String,
        systemPromptText: String
    ): String? =
        chatClient.prompt()
            .system(systemPromptText)
            .user(userPromptText)
            .call()
            .content()

    fun getAllMessages(
        conversationId: String
    ): List<Message> = chatMemory.get(conversationId, 100)

    fun clearConversation(
        conversationId: String
    ) = chatMemory.clear(conversationId)
}
