package dev.natig.gandalf.agents.stylist

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
        @RequestParam userTextPrompt: String,
        @RequestParam(required = false) image: MultipartFile?
    ): String? {
        val payload = if (image != null && !image.isEmpty) {
            "$userTextPrompt [User attached an image for analysis]"
        } else {
            userTextPrompt
        }
        return stylistService.processRequest(payload)
    }
}