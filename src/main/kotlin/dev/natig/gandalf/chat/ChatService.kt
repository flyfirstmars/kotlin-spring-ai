package dev.natig.gandalf.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.reactive.asFlow
import kotlinx.serialization.json.Json
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.Media
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MultipartFile


@Service
class ChatService(
    private val chatModel: ChatModel
) {

    suspend fun getChatCompletion(promptText: String): Response {
        val userMessage = UserMessage(promptText)
        val systemMessage = SystemMessage(Prompts.CHAT_SYSTEM_PROMPT)

        val prompt = Prompt(
            listOf(systemMessage, userMessage)
        )

        val response: ChatResponse = chatModel
            .call(prompt)

        val jsonResponse = response
            .results.first()
            .output
            .content

        return Json.decodeFromString<Response>(jsonResponse)
    }

    fun streamChatCompletion(promptText: String): Flow<String> {
        val userMessage = UserMessage(promptText)
        val systemMessage = SystemMessage(Prompts.CHAT_SYSTEM_PROMPT)
        val prompt = Prompt(listOf(systemMessage, userMessage))

        return chatModel.stream(prompt).asFlow().mapNotNull { chatResponse ->
            val jsonResponse = chatResponse.results.firstOrNull()?.output?.content
            jsonResponse?.takeIf { it.isNotBlank() } // Return non-blank JSON response
        }
    }

    suspend fun userMessageWithMediaType(promptText: String, file: MultipartFile): Response {
        val mimeType = when (file.contentType) {
            "image/jpeg" -> MimeTypeUtils.IMAGE_JPEG
            "image/png" -> MimeTypeUtils.IMAGE_PNG
            else -> throw IllegalArgumentException("unsupported image format")
        }

        val resource = ByteArrayResource(file.bytes)
        val media = Media(mimeType, resource)

        val userMessage = UserMessage(promptText, listOf(media))
        val systemMessage = SystemMessage(Prompts.IMAGE_ANALYSIS_SYSTEM_PROMPT)

        val prompt = Prompt(
            listOf<Message>(systemMessage, userMessage)
        )

        val response = chatModel.call(prompt)

        val jsonResponse = response
            .results.first()
            .output
            .content

        return Json.decodeFromString<Response>(jsonResponse)
    }

}