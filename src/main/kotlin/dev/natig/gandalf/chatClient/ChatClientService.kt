package dev.natig.gandalf.chatClient

import dev.natig.gandalf.common.Prompts
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.InMemoryChatMemory
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

    @Suppress("UNCHECKED_CAST")
    fun getAllMessages(conversationId: String): List<MessageWrapper> {
        val messages = chatMemory.get(conversationId, MAX_MESSAGES)
        return messages.map { message ->
            MessageWrapper(
                originalMessage = message,
                media = message.metadata["media"] as? List<Any> ?: emptyList(),
                toolCalls = message.metadata["toolCalls"] as? List<Any> ?: emptyList()
            )
        }
    }

    fun clearConversation(conversationId: String) = chatMemory.clear(conversationId)

    companion object {
        private const val MAX_MESSAGES = 100
    }
}