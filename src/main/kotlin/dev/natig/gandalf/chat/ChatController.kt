package dev.natig.gandalf.chat

import dev.natig.gandalf.chat.selector.Implementation
import dev.natig.gandalf.chat.selector.ImplementationSelectorProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Validated
@Tag(name = "Chat", description = "Conversational AI without context")
@RestController
class ChatController(private val implementationSelectorProvider: ImplementationSelectorProvider) {

    @Operation(summary = "Get chat completion", description = "Gets a chat response based on the provided prompt")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully retrieved the chat response"),
            ApiResponse(responseCode = "400", description = "Bad request, possibly due to invalid prompt"),
            ApiResponse(responseCode = "404", description = "Service not found"),
            ApiResponse(responseCode = "503", description = "Service unavailable, possibly due to upstream issues"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @GetMapping("/{implementation}/chat")
    suspend fun chat(
        @PathVariable implementation: Implementation,
        @RequestParam prompt: String
    ): String = implementationSelectorProvider
        .provide(implementation)
        .getChatCompletionWithTextPrompts(prompt)

    @Operation(summary = "Analyze image", description = "Analyzes the uploaded image and provides a response")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully analyzed the image"),
            ApiResponse(responseCode = "400", description = "Bad request, possibly due to unsupported image format"),
            ApiResponse(responseCode = "404", description = "Service not found"),
            ApiResponse(responseCode = "503", description = "Service unavailable, possibly due to upstream issues"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @PostMapping("/{implementation}/analyze-image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun analyzeImage(
        @PathVariable implementation: Implementation,
        @RequestPart image: MultipartFile
    ): String = implementationSelectorProvider
        .provide(implementation)
        .getChatCompletionWithImageAnalysis(image)

    @Operation(summary = "Stream chat completions", description = "Streams chat responses based on the provided prompt")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully streaming the chat response"),
            ApiResponse(responseCode = "400", description = "Bad request, possibly due to invalid prompt"),
            ApiResponse(responseCode = "404", description = "Service not found"),
            ApiResponse(responseCode = "503", description = "Service unavailable, possibly due to upstream issues"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @GetMapping("/{implementation}/stream-chat", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamChat(
        @PathVariable implementation: Implementation,
        @RequestParam prompt: String
    ): Flow<String> = implementationSelectorProvider
        .provide(implementation)
        .streamChatCompletion(prompt)
}