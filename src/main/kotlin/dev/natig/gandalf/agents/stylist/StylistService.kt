package dev.natig.gandalf.agents.stylist


import dev.natig.gandalf.agents.stylist.STYLIST.SYSTEM_PROMPT
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class StylistService(
    private val chatClient: ChatClient
) {
    fun processRequest(payload: String): String? =
        chatClient.prompt()
            .system(SYSTEM_PROMPT)
            .user(payload)
            .call()
            .content()
}