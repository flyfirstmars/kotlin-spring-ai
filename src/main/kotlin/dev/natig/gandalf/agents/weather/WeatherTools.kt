package dev.natig.gandalf.agents.weather

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service
import kotlinx.coroutines.runBlocking

@Service
class WeatherTools(private val weatherClient: WeatherClient) {

    @Tool(
        description = "Retrieve current weather information for a given location. Provide the location as a string (e.g., 'Tbilisi').",
        returnDirect = true
    )
    fun getCurrentWeather(
        @ToolParam(description = "The name of the location (e.g., 'Tbilisi')") location: String
    ): WeatherResponse? = runBlocking {
        weatherClient.fetchWeather(location)
    }
}