package dev.natig.gandalf.agents.orchestrator

import dev.natig.gandalf.chatClient.ChatResponse
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(
    name = "Orchestrator-Worker Workflow"
)
@RestController
@RequestMapping("/v1/orchestrator")
class OrchestratorController(private val orchestratorService: OrchestratorService) {

    @PostMapping("/process", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun processRequest(
        @RequestParam(required = false) conversationId: String?,
        @RequestParam(required = false) image: MultipartFile?,
        @RequestBody message: String
    ): ChatResponse {
        return orchestratorService.processRequest(conversationId, message, image)
    }
}