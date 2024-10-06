package dev.natig.gandalf.chat

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Validated
@Tag(name = "Chat Controller", description = "Chat Service that interacts with the OpenAI ChatCompletions API")
@RestController
class ChatController(private val chatService: ChatService) {

    private val logger = LoggerFactory.getLogger(ChatController::class.java)

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
    @GetMapping("/chat")
    suspend fun chat(@RequestParam prompt: String): Response {
        logger.info("Received chat request with prompt: $prompt")
        return chatService.getChatCompletion(prompt)
    }

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
    @PostMapping("/analyze-image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun analyzeImage(@RequestParam prompt: String, @RequestPart file: MultipartFile): Response {
        logger.info("Received image analysis request with file: ${file.originalFilename}")
        return chatService.userMessageWithMediaType(prompt, file)
    }

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
    @GetMapping("/stream-chat", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamChat(@RequestParam prompt: String): Flow<String> {
        logger.info("Received stream chat request with prompt: $prompt")
        return flow {
            val jsonBuilder = StringBuilder()

            chatService.streamChatCompletion(prompt).collect { jsonFragment ->
                if (!currentCoroutineContext().isActive) return@collect // Handle flow cancellation

                jsonBuilder.append(jsonFragment)
                val currentJson = jsonBuilder.toString()

                try {
                    if (currentJson.contains("\"final_answer\"")) {
                        val response = Json.decodeFromString<Response>(currentJson)
                        response.steps.forEach { step ->
                            emit("${step.explanation}\n\n")
                            emit("${step.output}\n\n")
                        }
                        emit("${response.final_answer}\n\n")
                        jsonBuilder.clear() // Clear builder after successful parsing
                    } else {
                        val stepJson = Json.decodeFromString<Step>(currentJson)
                        emit("${stepJson.explanation}\n\n")
                        emit("${stepJson.output}\n\n")
                        jsonBuilder.clear() // Clear builder after successful parsing
                    }
                } catch (e: SerializationException) {
                    logger.warn("Incomplete JSON received, waiting for more data.", e)
                }
            }
        }
    }
}