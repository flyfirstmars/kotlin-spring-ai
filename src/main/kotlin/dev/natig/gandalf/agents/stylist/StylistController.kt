package dev.natig.gandalf.agents.stylist

import dev.natig.gandalf.chatClient.ChatResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(
    name = "Multi-agent Interaction"
)
@RestController
@RequestMapping("/v1/stylist")
class StylistController(private val stylistService: StylistService) {

    @Operation(
        summary = "Stylist Agent as Conversational AI",
        description = "Gets clothing suggestions based on the provided image or text"
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
    @PostMapping("/advice", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun getAdvice(
        @RequestParam(required = false) conversationId: String?,
        @RequestParam userTextPrompt: String,
        @RequestParam(required = false) image: MultipartFile?
    ): ChatResponse {
        val payload = if (image != null && !image.isEmpty) {
            "[User attached an image for analysis] $userTextPrompt"
        } else {
            userTextPrompt
        }
        return stylistService.processRequest(conversationId, payload, image)
    }
}