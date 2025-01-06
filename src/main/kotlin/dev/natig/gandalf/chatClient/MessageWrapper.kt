package dev.natig.gandalf.chatClient

import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.MessageType

/**
 * A wrapper class for the SDK's Message interface, providing additional fields and methods if needed.
 */
data class MessageWrapper(
    private val originalMessage: Message,
    val media: List<Any> = emptyList(),
    val toolCalls: List<Any> = emptyList()
) : Message {

    override fun getMessageType(): MessageType = originalMessage.messageType

    override fun getText(): String = originalMessage.text

    override fun getMetadata(): Map<String, Any> =
        originalMessage.metadata.filterValues { it != null }

    @Deprecated("Deprecated in Java")
    override fun getContent(): String = originalMessage.text
}
