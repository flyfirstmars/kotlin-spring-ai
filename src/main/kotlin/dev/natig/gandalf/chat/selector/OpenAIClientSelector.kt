//package dev.natig.gandalf.chat.selector
//
//import com.openai.client.OpenAIClient
//import com.openai.core.http.StreamResponse
//import com.openai.models.ChatCompletionChunk
//import com.openai.models.ChatCompletionCreateParams
//import com.openai.models.ChatCompletionMessageParam
//import com.openai.models.ChatCompletionUserMessageParam
//import com.openai.models.ChatModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flowOf
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Service
//import org.springframework.web.multipart.MultipartFile
//
//@Service
//class OpenAIClientSelector(
//    private val openAIClient: OpenAIClient
//) : ImplementationSelector {
//
//    private val logger = LoggerFactory.getLogger(OpenAIClientSelector::class.java)
//
//    override fun getChatCompletionWithTextPrompts(userTextPrompt: String): String =
//        runCatching {
//            val params = buildChatCompletionParams(userTextPrompt)
//            val response = openAIClient.chat()
//                .completions()
//                .create(params)
//
//            response.choices()
//                .firstOrNull()
//                ?.message()
//                ?.content()
//                ?.orElse("")
//                ?: ""
//        }.getOrElse { ex ->
//            logger.error("Error fetching chat completion with text prompt", ex)
//            "An error occurred while processing your request."
//        }
//
//    override fun getChatCompletionWithImageAnalysis(image: MultipartFile): String =
//        runCatching {
//            val prompt = buildImageAnalysisPrompt(image)
//            val params = buildChatCompletionParams(prompt)
//            val response = openAIClient.chat()
//                .completions()
//                .create(params)
//
//            response.choices()
//                .firstOrNull()
//                ?.message()
//                ?.content()
//                ?.orElse("")
//                ?: ""
//        }.getOrElse { ex ->
//            logger.error("Error fetching chat completion with image analysis", ex)
//            "An error occurred while processing your image."
//        } override fun streamChatCompletion(userTextPrompt: String): Flow<String> = callbackFlow {
//            val job = launch(Dispatchers.IO) {
//                try {
//                    val params = buildChatCompletionParams(userTextPrompt)
//                    val streamResponse: StreamResponse<ChatCompletionChunk> =
//                        openAIClient.chat().completions().createStreaming(params)
//
//                    streamResponse.stream().use { chunkStream ->
//                        chunkStream.forEach { chunk ->
//                            val content = chunk.choices()
//                                .firstOrNull()
//                                ?.delta()
//                                ?.content()
//                                ?.orElse("")
//                                ?: ""
//                            trySend(content)
//                        }
//                    }
//                } catch (ex: Exception) {
//                    logger.error("Error streaming chat completion with text prompt", ex)
//                    trySend("An error occurred while streaming your request.")
//                } finally {
//                    // End the flow once we've exhausted or failed the stream
//                    close()
//                }
//            }
//
//            // Suspends until the stream is closed or an error occurs
//            awaitClose { job.cancel() }
//        }
//
//    override fun streamChatCompletionWithImageAnalysis(image: MultipartFile): Flow<String> = callbackFlow {
//        val job = launch(Dispatchers.IO) {
//            try {
//                val prompt = buildImageAnalysisPrompt(image)
//                val params = buildChatCompletionParams(prompt)
//                val streamResponse: StreamResponse<ChatCompletionChunk> =
//                    openAIClient.chat().completions().createStreaming(params)
//
//                streamResponse.stream().use { chunkStream ->
//                    chunkStream.forEach { chunk ->
//                        val content = chunk.choices()
//                            .firstOrNull()
//                            ?.delta()
//                            ?.content()
//                            ?.orElse("")
//                            ?: ""
//                        trySend(content)
//                    }
//                }
//            } catch (ex: Exception) {
//                logger.error("Error streaming chat completion with image analysis", ex)
//                trySend("An error occurred while streaming your image analysis.")
//            } finally {
//                close()
//            }
//        }
//
//        awaitClose { job.cancel() }
//    }
//
//    /**
//     * Constructs the ChatCompletionCreateParams with the given content.
//     */
//    private fun buildChatCompletionParams(content: String): ChatCompletionCreateParams =
//        ChatCompletionCreateParams.builder()
//            .model(ChatModel.GPT_4O_MINI) // Adjust to your desired model
//            .maxTokens(1024)
//            .addMessage(
//                ChatCompletionMessageParam.ofChatCompletionUserMessageParam(
//                    ChatCompletionUserMessageParam.builder()
//                        .role(ChatCompletionUserMessageParam.Role.USER)
//                        .content(ChatCompletionUserMessageParam.Content.ofTextContent(content))
//                        .build()
//                )
//            )
//            .build()
//
//    /**
//     * Builds a textual prompt describing the image (placeholder logic).
//     * Replace with actual image recognition or analysis logic as needed.
//     */
//    private fun buildImageAnalysisPrompt(image: MultipartFile): String {
//        // e.g., call an image recognition service, or convert image to Base64
//        return "Analyze the following image and provide insights."
//    }
//}