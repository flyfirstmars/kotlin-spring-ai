package dev.natig.gandalf.agents.scheduler

import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service

@Service
class SchedulerTools(private val repository: SchedulerRepository) {

    @Tool(description = "Retrieve scheduled events for a given date/time. Provide the date/time in ISO-8601 format (e.g., '2025-02-16' or '2025-02-16T08:30:00').")
    fun getEvents(
        @ToolParam(description = "Date/time in ISO-8601 format (e.g., '2025-02-16' for start of day, or '2025-02-16T08:30:00' for a specific time)") date: String
    ): String {
        val localDateTime = if (date.contains("T")) {
            LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        } else {
            LocalDateTime.parse("${date}T00:00:00", DateTimeFormatter.ISO_DATE_TIME)
        }
        val events = repository.getEvents(localDateTime)
        return events.joinToString(separator = ", ", prefix = "[", postfix = "]") { event ->
            """{"id":${event.id},"title":"${event.title}","datetime":"${event.datetime}","location":"${event.location}","active":${event.active}}"""
        }
    }

    @Tool(description = "Add a new scheduled event. Provide event details as a JSON string containing title, datetime (ISO-8601 format), and location.")
    fun addEvent(
        @ToolParam(description = "Event details in JSON (e.g., {\"title\":\"Meeting\",\"datetime\":\"2025-02-16T10:00:00\",\"location\":\"Conference Room A\"})") eventJson: String
    ): String {
        val request = Json.decodeFromString<AddEventRequest>(eventJson)
        val newEvent = Event(
            id = System.currentTimeMillis(),
            title = request.title,
            datetime = request.datetime,
            location = request.location
        )
        val success = repository.addEvent(newEvent)
        return if (success) "Event added successfully." else "Failed to add event."
    }

    @Tool(description = "Update an existing scheduled event. Provide updated event details as a JSON string containing id, title, datetime (ISO-8601), location, and active status.")
    fun updateEvent(
        @ToolParam(description = "Updated event details in JSON (e.g., {\"id\":123,\"title\":\"Updated Meeting\",\"datetime\":\"2025-02-16T15:00:00\",\"location\":\"Conference Room B\",\"active\":true})") eventJson: String
    ): String {
        val request = Json.decodeFromString<UpdateEventRequest>(eventJson)
        val updatedEvent = Event(
            id = request.id,
            title = request.title,
            datetime = request.datetime,
            location = request.location,
            active = request.active
        )
        val success = repository.updateEvent(updatedEvent)
        return if (success) "Event updated successfully." else "Failed to update event."
    }

    @Tool(description = "Soft delete a scheduled event by marking it inactive. Provide the event ID as a string. The event will remain recoverable for 30 days.")
    fun deleteEvent(
        @ToolParam(description = "ID of the event to soft delete") eventIdStr: String
    ): String {
        val eventId = eventIdStr.toLongOrNull() ?: return "Invalid event ID."
        val success = repository.softDeleteEvent(eventId)
        return if (success) "Event marked as inactive. It can be restored within 30 days by updating it." else "Failed to delete event."
    }
}