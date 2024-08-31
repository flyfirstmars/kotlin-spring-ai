package dev.natig.gandalf.chat

import java.util.Base64
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
            .results[0]
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

    suspend fun userMessageWithMediaType(file: MultipartFile): Response {
        val base64Image = encodeImageToBase64(file)
        val media = Media(MimeTypeUtils.IMAGE_JPEG, base64Image)
        val userMessage = UserMessage("Analyze this image", listOf(media))
        val systemMessage = SystemMessage(Prompts.IMAGE_ANALYSIS_SYSTEM_PROMPT)

        val prompt = Prompt(
            listOf<Message>(systemMessage, userMessage)
        )

        val response = chatModel.call(prompt)

        val jsonResponse = response
            .results[0]
            .output
            .content

        return Json.decodeFromString<Response>(jsonResponse)
    }

    private suspend fun encodeImageToBase64(file: MultipartFile): String {
        val prefix = when (file.contentType) {
            "image/jpeg" -> "data:image/jpeg;base64,"
            "image/png" -> "data:image/png;base64,"
            else -> throw IllegalArgumentException("unsupported image format")
        }

        return prefix + Base64.getEncoder().encodeToString(file.bytes)
    }

}