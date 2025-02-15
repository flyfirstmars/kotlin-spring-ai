package dev.natig.gandalf.chatModel

import dev.natig.gandalf.common.Prompts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.reactive.asFlow
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ChatModelService(
    private val chatModel: ChatModel,
    private val promptBuilder: PromptBuilder
) {

    fun getChatCompletionWithTextPrompts(userTextPrompt: String): String =
        chatModel.getSingleResponse(
            promptBuilder
                .systemMessage(Prompts.CHAT_SYSTEM_PROMPT)
                .userMessage(userTextPrompt)
                .build()
        )

    fun getChatCompletionWithImageAnalysis(image: MultipartFile): String =
        chatModel.getSingleResponse(
            promptBuilder
                .systemMessage(Prompts.IMAGE_ANALYSIS_SYSTEM_PROMPT)
                .userMessage("Analyze this image", image)
                .build()
        )

    fun streamChatCompletion(userTextPrompt: String): Flow<String> =
        chatModel.getStreamingResponse(
            promptBuilder
                .systemMessage(Prompts.CHAT_SYSTEM_PROMPT)
                .userMessage(userTextPrompt)
                .build()
        )

    fun streamChatCompletionWithImageAnalysis(image: MultipartFile): Flow<String> =
        chatModel.getStreamingResponse(
            promptBuilder
                .systemMessage(Prompts.IMAGE_ANALYSIS_SYSTEM_PROMPT)
                .userMessage("Analyze this image", image)
                .build()
        )

    private fun ChatModel.getSingleResponse(prompt: Prompt): String =
        call(prompt).results.firstOrNull()?.output?.text.orEmpty()

    private fun ChatModel.getStreamingResponse(prompt: Prompt): Flow<String> =
        stream(prompt)
            .asFlow()
            .mapNotNull { chatResponse ->
                chatResponse
                    .results
                    .firstOrNull()
                    ?.output
                    ?.text
                    ?.takeIf { it.isNotBlank() }
            }
}