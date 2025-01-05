package dev.natig.gandalf.chatClient

import dev.natig.gandalf.common.Prompts
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.UUID
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.ai.chat.messages.Message
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "SpringAI ChatClient",
    description = "Abstraction-based agentic interaction with LLM APIs"
)
@RestController
@RequestMapping("/v1/chat-client")
class ChatClientController(
    private val chatClientService: ChatClientService,
    private val chatMemory: InMemoryChatMemory
) {

    @Operation(
        summary = "Get chat completion with context management",
        description = "Gets a chat response based on the provided prompt and logic type"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved the chat response"),
            ApiResponse(responseCode = "400", description = "Bad request, possibly due to invalid prompt"),
            ApiResponse(responseCode = "404", description = "Service not found"),
            ApiResponse(responseCode = "503", description = "Service unavailable, possibly due to upstream issues"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @PostMapping("/chat", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getChatResponse(
        @RequestParam(required = false) conversationId: String?,
        @RequestParam logicType: LogicType,
        @RequestBody message: String
    ): ChatResponse {
        return when (logicType) {
            LogicType.ADVISOR -> {
                val resolvedConversationId = getOrCreateConversationId(conversationId)
                val response = chatClientService.getChatClientResponse(
                    message,
                    Prompts.CHAT_SYSTEM_PROMPT,
                    resolvedConversationId
                ).orEmpty()
                ChatResponse(conversationId = resolvedConversationId, response = response)
            }

            LogicType.NO_ADVISOR -> {
                val response = chatClientService.getNoAdvisorChatClientResponse(
                    message,
                    Prompts.CHAT_SYSTEM_PROMPT
                ).orEmpty()
                ChatResponse(conversationId = "", response = response)
            }
        }
    }

    @Operation(
        summary = "Get all messages stored in a conversation"
    )
    @GetMapping("/conversation-messages/{conversationId}")
    fun getAllMessages(
        @PathVariable conversationId: String
    ): List<Message> = chatMemory.get(conversationId, 100)

    @Operation(
        summary = "Remove messages from the conversation"
    )
    @DeleteMapping("/{conversationId}")
    fun clearConversation(
        @PathVariable conversationId: String
    ) = chatMemory.clear(conversationId)

    private fun getOrCreateConversationId(conversationId: String?): String =
        conversationId ?: UUID.randomUUID().toString().also {
            chatMemory.add(it, emptyList())
        }

    enum class LogicType {
        ADVISOR,
        NO_ADVISOR
    }

    data class ChatResponse(
        val conversationId: String,
        val response: String
    )
}
