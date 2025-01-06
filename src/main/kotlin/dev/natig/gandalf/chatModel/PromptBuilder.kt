package dev.natig.gandalf.chatModel

import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.Media
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import org.springframework.web.multipart.MultipartFile

@Service
class PromptBuilder {

    private val messages = mutableListOf<Message>()

    fun systemMessage(content: String): PromptBuilder = apply {
        messages.add(SystemMessage(content))
    }

    fun userMessage(content: String, media: MultipartFile? = null): PromptBuilder = apply {
        val mediaList = media?.let { listOf(createMediaFromImage(it)) }.orEmpty()
        messages.add(UserMessage(content, mediaList))
    }

    fun build(): Prompt = Prompt(messages.toList())

    private fun createMediaFromImage(image: MultipartFile): Media =
        Media(resolveMimeType(image), ByteArrayResource(image.bytes))

    private fun resolveMimeType(image: MultipartFile): MimeType =
        when (image.contentType) {
            MimeTypeUtils.IMAGE_JPEG_VALUE -> MimeTypeUtils.IMAGE_JPEG
            MimeTypeUtils.IMAGE_PNG_VALUE -> MimeTypeUtils.IMAGE_PNG
            else -> throw IllegalArgumentException("Unsupported image type: ${image.contentType}")
        }
}