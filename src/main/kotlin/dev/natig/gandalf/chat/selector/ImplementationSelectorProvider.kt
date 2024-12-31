package dev.natig.gandalf.chat.selector

import org.springframework.stereotype.Service

@Service
class ImplementationSelectorProvider(
    private val springAIChatModelSelector: SpringAIChatModelSelector,
    private val openAIClientSelector: OpenAIClientSelector
) {

    fun provide(implementation: Implementation): ImplementationSelector {
        return when(implementation) {
            Implementation.SPRING_AI_CHAT_MODEL -> springAIChatModelSelector
            Implementation.OPENAI_JAVA -> openAIClientSelector
        }
    }
}