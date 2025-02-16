package dev.natig.gandalf.agents.weather

import dev.natig.gandalf.agents.tools.DateTimeTools
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class WeatherService(
    private val chatClient: ChatClient,
    private val dateTimeTools: DateTimeTools,
    private val weatherTools: WeatherTools
) {

    fun processRequest(
        payload: String
    ): String? = chatClient.prompt()
        .system(WEATHER.SYSTEM_PROMPT)
        .user(payload)
        .tools(weatherTools, dateTimeTools)
        .call()
        .content()
}