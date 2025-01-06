package dev.natig.gandalf.chatClient

import dev.natig.gandalf.common.Prompts
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
        logicType: LogicType,
        message: String
    ): ChatResponse {
        val resolvedConversationId = when (logicType) {
            LogicType.ADVISOR -> conversationId.resolveConversationIdWithMemory(chatMemory)
            LogicType.NO_ADVISOR -> conversationId.orEmpty()
        }

        val response = when (logicType) {
            LogicType.ADVISOR -> chatClient.getAdvisorResponse(
                message,
                Prompts.CHAT_SYSTEM_PROMPT,
                resolvedConversationId
            )

            LogicType.NO_ADVISOR -> chatClient.getNoAdvisorResponse(
                message,
                Prompts.CHAT_SYSTEM_PROMPT
            )
        }

        return ChatResponse(
            conversationId = resolvedConversationId,
            response = response.orEmpty()
        )
    }

    fun getAllMessages(conversationId: String): List<Message> = chatMemory.get(conversationId, MAX_MESSAGES)

    fun clearConversation(conversationId: String) = chatMemory.clear(conversationId)

    companion object {
        private const val MAX_MESSAGES = 100
    }
}