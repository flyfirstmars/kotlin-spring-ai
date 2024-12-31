//package dev.natig.gandalf.chat.client
//
//import java.util.UUID
//import org.springframework.ai.chat.memory.InMemoryChatMemory
//import org.springframework.stereotype.Service
//
//@Service
//class CeleronService(
//    private val chatClientService: ChatClientService,
//    private val chatMemory: InMemoryChatMemory
//) {
//
//    fun getAgentResponse(
//        userMessage: String,
//        conversationId: String?
//    ): String? {
//        return chatClientService
//            .getChatClientResponse(userMessage, getOrCreateConversationId(conversationId))
//    }
//
//    private fun getOrCreateConversationId(
//        conversationId: String?
//    ): String = conversationId ?: UUID.randomUUID().toString().also { chatMemory.add(it, emptyList()) }
//}
//
//
