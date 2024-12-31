package dev.natig.gandalf.chat.selector

import dev.natig.gandalf.chat.Prompts

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.reactive.asFlow
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.Media
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MultipartFile

@Service
class SpringAIChatModelSelector(
    private val chatModel: ChatModel
) : ImplementationSelector {

    override fun getChatCompletionWithTextPrompts(userTextPrompt: String): String =
        getChatModelContent(chatModel, PromptBuilder.buildTextPrompt(userTextPrompt))

    override fun getChatCompletionWithImageAnalysis(image: MultipartFile): String =
        getChatModelContent(chatModel, PromptBuilder.buildImageAnalysisPrompt(image))

    override fun streamChatCompletion(userTextPrompt: String): Flow<String> =
        streamChatModelContent(chatModel, PromptBuilder.buildTextPrompt(userTextPrompt))

    override fun streamChatCompletionWithImageAnalysis(image: MultipartFile): Flow<String> =
        streamChatModelContent(chatModel, PromptBuilder.buildImageAnalysisPrompt(image))

    private fun getChatModelContent(chatModel: ChatModel, prompt: Prompt): String =
        chatModel.call(prompt)
            .results.first()
            .output
            .content

    private fun streamChatModelContent(chatModel: ChatModel, prompt: Prompt): Flow<String> =
        chatModel.stream(prompt)
            .asFlow()
            .mapNotNull { chatResponse ->
                chatResponse
                    .results
                    .firstOrNull()
                    ?.output
                    ?.content
                    ?.takeIf { it.isNotBlank() }
            }

    private object PromptBuilder {
        fun buildTextPrompt(userTextPrompt: String): Prompt =
            Prompt(
                listOf(
                    SystemMessage(Prompts.CHAT_SYSTEM_PROMPT),
                    UserMessage(userTextPrompt)
                )
            )

        fun buildImageAnalysisPrompt(image: MultipartFile): Prompt {
            val media = createMediaFromImage(image)
            val userMessage = UserMessage("Analyze this image", listOf(media))
            val systemMessage = SystemMessage(Prompts.IMAGE_ANALYSIS_SYSTEM_PROMPT)
            return Prompt(listOf(systemMessage, userMessage))
        }

        private fun createMediaFromImage(image: MultipartFile): Media =
            Media(resolveMimeType(image), ByteArrayResource(image.bytes))

        private fun resolveMimeType(image: MultipartFile): MimeType =
            when (image.contentType) {
                MimeTypeUtils.IMAGE_JPEG_VALUE -> MimeTypeUtils.IMAGE_JPEG
                MimeTypeUtils.IMAGE_PNG_VALUE -> MimeTypeUtils.IMAGE_PNG
                else -> throw IllegalArgumentException("Unsupported image type ${image.contentType}")
            }
    }

}