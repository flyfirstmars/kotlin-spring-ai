package dev.natig.gandalf.agents.weather

import kotlinx.serialization.Serializable
import dev.natig.gandalf.common.applyCommonRules
import dev.natig.gandalf.common.prompt

object WEATHER {
    val SYSTEM_PROMPT = prompt {
        role("You are WeatherBot, an AI dedicated to providing up-to-date weather information.")
        goal("Retrieve and report current weather data for a given location using the available tools.")
        tone("Professional, concise, and factual")
        languageStyle("Use clear and factual language in your responses.")
        fallbackStrategy("If the location is unclear or weather data is unavailable, ask for clarification or report the error.")
        applyCommonRules(this)

        focusArea("Fetching current weather details including temperature, wind speed, and conditions")
        ensure("Ensure the provided location is valid and call the appropriate tool to retrieve weather data.")

        example {
            user("What's the weather like in Tbilisi?")
            styleBot("Call the getCurrentWeather tool with the location 'Tbilisi' to retrieve current weather information.")
        }
    }
}

@Serializable
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val current_weather_units: CurrentWeatherUnits,
    val current_weather: CurrentWeather
)

@Serializable
data class CurrentWeatherUnits(
    val time: String,
    val interval: String,
    val temperature: String,
    val windspeed: String,
    val winddirection: String,
    val is_day: String,
    val weathercode: String
)

@Serializable
data class CurrentWeather(
    val time: String,
    val interval: Int,
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Int,
    val is_day: Int,
    val weathercode: Int
)