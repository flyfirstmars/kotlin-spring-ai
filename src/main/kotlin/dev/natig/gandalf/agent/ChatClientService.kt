package dev.natig.gandalf.agent

import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class ChatClientService(
    private val chatClient: ChatClient
) {

    fun getChatClientResponse(
        userPromptText: String,
        systemPromptText: String,
        conversationId: String,
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
        systemPromptText: String,
        conversationId: String,
    ): String? =
        chatClient.prompt()
            .system(systemPromptText)
            .user(userPromptText)
            .call()
            .content()



}
