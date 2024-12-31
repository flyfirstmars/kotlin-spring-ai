//package dev.natig.celeron
//
//import org.springframework.http.MediaType
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestParam
//import org.springframework.web.bind.annotation.RestController
//
//@RestController
//class CeleronController(
//    private val celeronService: CeleronService
//) {
//
//    @PostMapping("/chat", produces = [MediaType.APPLICATION_JSON_VALUE])
//    fun getChatResponse(
//        @RequestParam(required = false) conversationId: String?,
//        @RequestBody message: String
//    ): String = celeronService.getAgentResponse(message, conversationId).toString()
//}