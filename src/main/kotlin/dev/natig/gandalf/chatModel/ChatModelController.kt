package dev.natig.gandalf.chatModel

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
        description = "Gets a chat response or streams responses based on the response type"
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
    @PostMapping("/chat-completion", produces = [MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE])
    fun chatCompletion(
        @RequestBody userPrompt: String,
        @RequestParam responseType: ResponseType
    ): Any = when (responseType) {
        ResponseType.NON_STREAM -> chatModelService.getChatCompletionWithTextPrompts(userPrompt)
        ResponseType.STREAM -> chatModelService.streamChatCompletion(userPrompt)
    }

    @Operation(
        summary = "Analyze image",
        description = "Analyzes the uploaded image and provides a response or streams responses based on the response type"
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
    @PostMapping(
        "/image-analysis",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE]
    )
    fun imageAnalysis(
        @RequestPart image: MultipartFile,
        @RequestParam responseType: ResponseType
    ): Any = when (responseType) {
        ResponseType.NON_STREAM -> chatModelService.getChatCompletionWithImageAnalysis(image)
        ResponseType.STREAM -> chatModelService.streamChatCompletionWithImageAnalysis(image)
    }

    enum class ResponseType {
        STREAM,
        NON_STREAM
    }
}



