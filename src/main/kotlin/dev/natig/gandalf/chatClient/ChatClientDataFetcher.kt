package dev.natig.gandalf.chatClient

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class ChatClientDataFetcher(private val chatClientService: ChatClientService) {

    @DgsQuery
    fun getChatResponse(
        @InputArgument conversationId: String?,
        @InputArgument logicType: LogicType,
        @InputArgument message: String
    ): ChatResponse {
        return chatClientService.getChatResponse(conversationId, logicType, message.toString())
    }

    @DgsQuery
    fun getAllMessages(@InputArgument conversationId: String): List<MessageWrapper> {
        return chatClientService.getAllMessages(conversationId)
    }

    @DgsMutation
    fun clearConversation(@InputArgument conversationId: String): Boolean {
        chatClientService.clearConversation(conversationId)
        return true
    }
}
