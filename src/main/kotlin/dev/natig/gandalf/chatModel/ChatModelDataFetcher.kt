package dev.natig.gandalf.chatModel

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.DgsSubscription
import com.netflix.graphql.dgs.InputArgument
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import org.springframework.web.multipart.MultipartFile

@DgsComponent
class ChatModelDataFetcher(
    private val chatModelService: ChatModelService
) {

    @DgsQuery
    fun chatCompletion(
        @InputArgument userPrompt: String
    ): String = chatModelService.getChatCompletionWithTextPrompts(userPrompt)

    @DgsMutation
    fun imageAnalysis(dfe: DataFetchingEnvironment): String {
        val image: MultipartFile? = dfe.getArgument("image")
        requireNotNull(image) { "Image file must be provided" }
        return chatModelService.getChatCompletionWithImageAnalysis(image)
    }
    @DgsSubscription
    fun streamChatCompletion(
        @InputArgument userPrompt: String
    ): Flow<String> = chatModelService.streamChatCompletion(userPrompt)

    @DgsSubscription
    fun streamChatCompletionWithImageAnalysis(
        @InputArgument image: MultipartFile
    ): Flow<String> = chatModelService.streamChatCompletionWithImageAnalysis(image)

}
