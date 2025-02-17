package dev.natig.gandalf.agents.stylist

import dev.natig.gandalf.agents.scheduler.SchedulerService
import dev.natig.gandalf.agents.weather.WeatherService
import kotlinx.coroutines.runBlocking
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service

@Service
class StylistTools(
    private val schedulerService: SchedulerService,
    private val weatherService: WeatherService
) {

    @Tool(
        description = "Retrieve weather information using the weather agent. " +
                "Provide a text payload such as 'Weather in Tbilisi' to obtain the current weather data via the weather agent."
    )
    fun retrieveWeatherData(
        @ToolParam(description = "Text payload for weather inquiry (e.g., 'Weather in Tbilisi')") payload: String
    ): String? = runBlocking {
        weatherService.processRequest(payload)
    }

    @Tool(
        description = "Retrieve your scheduling information from the scheduler agent. " +
                "Provide a text query such as 'Show me my events for today' to view scheduled events."
    )
    fun retrieveSchedule(
        @ToolParam(description = "Text query for retrieving scheduling details (e.g., 'Show me my events for today')") query: String
    ): String = runBlocking {
        schedulerService.processRequest(query) ?: "No scheduling information available."
    }

    @Tool(
        description = "Schedule a new event via the scheduler agent. " +
                "Provide event details as a text command (e.g., 'Add an event: Team Meeting at 10:00 on 2025-02-17 at Conference Room A')."
    )
    fun scheduleNewEvent(
        @ToolParam(description = "Text command for scheduling a new event") command: String
    ): String = runBlocking {
        schedulerService.processRequest(command) ?: "Failed to schedule event."
    }

    @Tool(
        description = "Delete a scheduled event by its ID via the scheduler agent. " +
                "Provide the event ID as a text command (e.g., 'Delete my event with ID 456')."
    )
    fun deleteEvent(
        @ToolParam(description = "Text command to delete a scheduled event") command: String
    ): String = runBlocking {
        schedulerService.processRequest(command) ?: "Failed to delete event."
    }
}