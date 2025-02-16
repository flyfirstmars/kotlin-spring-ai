package dev.natig.gandalf.agents.scheduler

import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class InMemorySchedulerRepository : SchedulerRepository {
    private val logger = LoggerFactory.getLogger(InMemorySchedulerRepository::class.java)
    private val events = ConcurrentHashMap<Long, Event>()

    override fun getEvents(date: LocalDateTime): List<Event> {
        logger.info("getEvents called with date: $date")
        val result = events.values.filter {
            LocalDateTime.parse(it.datetime).toLocalDate() == date.toLocalDate()
        }
        logger.info("getEvents returning ${result.size} events for date: $date")
        return result
    }

    override fun addEvent(event: Event): Boolean {
        logger.info("addEvent called with event: $event")
        events[event.id] = event
        logger.info("Event added successfully: $event")
        return true
    }

    override fun updateEvent(event: Event): Boolean {
        logger.info("updateEvent called with event: $event")
        return if (events.containsKey(event.id)) {
            events[event.id] = event
            logger.info("Event updated successfully: $event")
            true
        } else {
            logger.warn("updateEvent failed: event with id ${event.id} not found")
            false
        }
    }

    override fun softDeleteEvent(eventId: Long): Boolean {
        logger.info("softDeleteEvent called for eventId: $eventId")
        val event = events[eventId]
        if (event == null) {
            logger.warn("softDeleteEvent failed: event with id $eventId not found")
            return false
        }
        val updatedEvent = event.copy(
            active = false,
            deletedAt = LocalDateTime.now().toString()
        )
        events[eventId] = updatedEvent
        logger.info("Event soft deleted successfully: $updatedEvent")
        return true
    }
}