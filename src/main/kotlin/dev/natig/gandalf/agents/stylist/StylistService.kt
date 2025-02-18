package dev.natig.gandalf.agents.stylist

import dev.natig.gandalf.agents.stylist.STYLIST.SYSTEM_PROMPT
import dev.natig.gandalf.agents.tools.DateTimeTools
import dev.natig.gandalf.chatClient.ChatResponse
import dev.natig.gandalf.chatClient.resolveConversationIdWithMemory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MultipartFile

@Service
class StylistService(
    private val chatClient: ChatClient,
    private val stylistTools: StylistTools,
    private val dateTimeTools: DateTimeTools,
    private val chatMemory: InMemoryChatMemory
) {

    fun processRequest(
        conversationId: String?,
        userTextPrompt: String,
        image: MultipartFile?
    ): ChatResponse {
        val resolvedConversationId = conversationId.resolveConversationIdWithMemory(chatMemory)

        val promptSpec = if (image != null && !image.isEmpty) {
            chatClient.prompt().system(SYSTEM_PROMPT).user { userSpec ->
                userSpec.text(userTextPrompt)
                userSpec.media(MimeTypeUtils.IMAGE_JPEG, ByteArrayResource(image.bytes))
            }
        } else {
            chatClient.prompt().system(SYSTEM_PROMPT).user(userTextPrompt)
        }

        val response = promptSpec
            .advisors { advisor ->
                advisor.param("chat_memory_conversation_id", resolvedConversationId)
                    .param("chat_memory_response_size", 20)
            }
            .tools(stylistTools, dateTimeTools)
            .call()
            .content()

        return ChatResponse(
            conversationId = resolvedConversationId,
            response = response.orEmpty()
        )
    }
}