package dev.natig.gandalf.agents.stylist

import dev.natig.gandalf.agents.stylist.STYLIST.SYSTEM_PROMPT
import dev.natig.gandalf.agents.tools.DateTimeTools
import dev.natig.gandalf.chatClient.ChatResponse
import dev.natig.gandalf.chatClient.resolveConversationIdWithMemory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.ai.evaluation.EvaluationResponse
import org.springframework.ai.evaluation.Evaluator

@Service
class StylistService(
    private val chatClient: ChatClient,
    private val stylistTools: StylistTools,
    private val dateTimeTools: DateTimeTools,
    private val chatMemory: InMemoryChatMemory,
    private val evaluator: Evaluator
) {

    fun processRequest(
        conversationId: String?,
        userTextPrompt: String,
        image: MultipartFile?
    ): ChatResponse = runBlocking {
        val resolvedConversationId = conversationId.resolveConversationIdWithMemory(chatMemory)
        var context = ""
        var generatedResponse = generateResponse(resolvedConversationId, userTextPrompt, image, context)
        var evaluationResponse = evaluateResponse(userTextPrompt, generatedResponse.response)
        var iteration = 0
        val maxIterations = 3

        while (!evaluationResponse.isPass && iteration < maxIterations) {
            context += "\nFeedback: ${evaluationResponse.feedback}"
            generatedResponse = generateResponse(resolvedConversationId, userTextPrompt, image, context)
            evaluationResponse = evaluateResponse(userTextPrompt, generatedResponse.response)
            iteration++
        }

        ChatResponse(
            conversationId = resolvedConversationId,
            response = generatedResponse.response
        )
    }

    private suspend fun generateResponse(
        conversationId: String,
        userTextPrompt: String,
        image: MultipartFile?,
        context: String
    ): ChatResponse = coroutineScope {
        val promptSpec = if (image != null && !image.isEmpty) {
            chatClient.prompt().system(SYSTEM_PROMPT).user { userSpec ->
                userSpec.text("$userTextPrompt\nContext: $context")
                userSpec.media(MimeTypeUtils.IMAGE_JPEG, ByteArrayResource(image.bytes))
            }
        } else {
            chatClient.prompt().system(SYSTEM_PROMPT).user("$userTextPrompt\nContext: $context")
        }

        val responseText = promptSpec
            .advisors { advisor ->
                advisor.param("chat_memory_conversation_id", conversationId)
                    .param("chat_memory_response_size", 20)
            }
            .tools(stylistTools, dateTimeTools)
            .call()
            .content()

        ChatResponse(conversationId = conversationId, response = responseText.orEmpty())
    }

    private suspend fun evaluateResponse(userText: String, response: String): EvaluationResponse {
        val evaluationRequest = EvaluationRequest(userText, emptyList(), response)
        return evaluator.evaluate(evaluationRequest)
    }
}