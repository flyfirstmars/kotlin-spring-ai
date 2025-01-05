package dev.natig.gandalf.chatClient

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.ai.chat.messages.Message
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
    private val chatClientService: ChatClientService
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
        return chatClientService.getChatResponse(conversationId, logicType, message)
    }

    @Operation(
        summary = "Get all messages stored in a conversation"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved the list of messages"),
            ApiResponse(responseCode = "404", description = "No messages found for the provided conversation ID")
        ]
    )
    @GetMapping("/conversation-messages/{conversationId}")
    fun getAllMessages(
        @PathVariable conversationId: String
    ): ResponseEntity<List<Message>> {
        val messages = chatClientService.getAllMessages(conversationId)
        return if (messages.isEmpty()) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(messages)
        }
    }

    @Operation(
        summary = "Remove messages from the conversation"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Successfully cleared the conversation"),
            ApiResponse(responseCode = "404", description = "Conversation not found")
        ]
    )
    @DeleteMapping("/{conversationId}")
    fun clearConversation(
        @PathVariable conversationId: String
    ): ResponseEntity<Void> {
        chatClientService.clearConversation(conversationId)
        return ResponseEntity.noContent().build()
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
