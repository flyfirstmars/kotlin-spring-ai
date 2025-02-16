package dev.natig.gandalf.agents.weather

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class WeatherController(
    private val weatherService: WeatherService
) {
    @GetMapping("/weather")
    suspend fun getWeather(
        @RequestParam city: String
    ): String? {
        return weatherService.processRequest(city)
    }
}
