package dev.natig.gandalf.chatModel

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(
    name = "SpringAI ChatModel",
    description = "Abstraction based stateless interaction with the LLM APIs"
)
@RestController
@RequestMapping("/v1/chat-model")
class ChatModelController(private val chatModelService: ChatModelService) {

    @Operation(
        summary = "Get chat completion",
        description = "Gets a chat response based on the provided prompt"
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
    @GetMapping("/chat")
    suspend fun chat(
        @RequestBody userPrompt: String
    ): String = chatModelService
        .getChatCompletionWithTextPrompts(userPrompt)

    @Operation(
        summary = "Analyze image",
        description = "Analyzes the uploaded image and provides a response"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully analyzed the image"),
            ApiResponse(responseCode = "400", description = "Bad request, possibly due to unsupported image format"),
            ApiResponse(responseCode = "404", description = "Service not found"),
            ApiResponse(responseCode = "503", description = "Service unavailable, possibly due to upstream issues"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @PostMapping("/image-analysis", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun imageAnalysisResponse(
        @RequestPart image: MultipartFile
    ): String = chatModelService
        .getChatCompletionWithImageAnalysis(image)

    @Operation(
        summary = "Stream chat completions",
        description = "Streams chat responses based on the provided prompt"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully streaming the chat response"),
            ApiResponse(responseCode = "400", description = "Bad request, possibly due to invalid prompt"),
            ApiResponse(responseCode = "404", description = "Service not found"),
            ApiResponse(responseCode = "503", description = "Service unavailable, possibly due to upstream issues"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @GetMapping("/stream-chat", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamChat(
        @RequestBody userPrompt: String
    ): Flow<String> = chatModelService
        .streamChatCompletion(userPrompt)

    @Operation(
        summary = "Analyze image and Stream Response",
        description = "Analyzes the uploaded image and provides a response as a stream"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successfully analyzed the image"),
            ApiResponse(responseCode = "400", description = "Bad request, possibly due to unsupported image format"),
            ApiResponse(responseCode = "404", description = "Service not found"),
            ApiResponse(responseCode = "503", description = "Service unavailable, possibly due to upstream issues"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @PostMapping("/stream-image-analysis", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun imageAnalysisStreamedResponse(
        @RequestPart image: MultipartFile
    ): Flow<String> = chatModelService
        .streamChatCompletionWithImageAnalysis(image)

}