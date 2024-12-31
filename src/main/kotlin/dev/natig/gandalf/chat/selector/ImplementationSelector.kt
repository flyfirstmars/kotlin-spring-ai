package dev.natig.gandalf.chat.selector

import kotlinx.coroutines.flow.Flow
import org.springframework.web.multipart.MultipartFile

interface ImplementationSelector {
    fun getChatCompletionWithTextPrompts(userTextPrompt: String): String
    fun getChatCompletionWithImageAnalysis(image: MultipartFile): String
    fun streamChatCompletion(userTextPrompt: String): Flow<String>
    fun streamChatCompletionWithImageAnalysis(image: MultipartFile): Flow<String>
}