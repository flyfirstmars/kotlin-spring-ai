package dev.natig.gandalf.agents.stylist

import dev.natig.gandalf.chatClient.ChatResponse
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/stylist")
class StylistController(private val stylistService: StylistService) {

    @PostMapping("/advice", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun getAdvice(
        @RequestParam(required = false) conversationId: String?,
        @RequestBody userTextPrompt: String,
        @RequestParam(required = false) image: MultipartFile?
    ): ChatResponse {
        return stylistService.processRequest(
            conversationId,
            userTextPrompt,
            image
        )
    }
}