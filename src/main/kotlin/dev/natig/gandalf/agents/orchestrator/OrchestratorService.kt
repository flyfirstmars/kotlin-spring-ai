package dev.natig.gandalf.agents.orchestrator

import dev.natig.gandalf.agents.scheduler.SchedulerService
import dev.natig.gandalf.agents.stylist.StylistService
import dev.natig.gandalf.agents.weather.WeatherService
import dev.natig.gandalf.chatClient.ChatResponse
import dev.natig.gandalf.chatClient.resolveConversationIdWithMemory
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class OrchestratorService(
    private val stylistService: StylistService,
    private val schedulerService: SchedulerService,
    private val weatherService: WeatherService,
    private val chatMemory: InMemoryChatMemory
) {

    fun processRequest(
        conversationId: String?,
        userTextPrompt: String,
        image: MultipartFile?
    ): ChatResponse = runBlocking {
        val resolvedConversationId = conversationId.resolveConversationIdWithMemory(chatMemory)

        val (stylistResponse, schedulerResponse, weatherResponse) = coroutineScope {
            val stylistDeferred = async {
                stylistService.processRequest(resolvedConversationId, userTextPrompt, image)
            }
            val schedulerDeferred = async {
                schedulerService.processRequest("Show me my schedule for today")
                    ?: "No scheduling information available."
            }
            val weatherDeferred = async {
                weatherService.processRequest("Weather in Tbilisi") ?: "No weather data available."
            }
            Triple(stylistDeferred.await(), schedulerDeferred.await(), weatherDeferred.await())
        }

        val combinedResponse = """
            Stylist Advice:
            ${stylistResponse.response}
            
            Schedule Information:
            $schedulerResponse
            
            Weather Information:
            $weatherResponse
        """.trimIndent()

        ChatResponse(
            conversationId = resolvedConversationId,
            response = combinedResponse
        )
    }
}