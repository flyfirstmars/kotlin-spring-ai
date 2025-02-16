package dev.natig.gandalf.agents.scheduler

import java.time.LocalDateTime

interface SchedulerRepository {
    fun getEvents(date: LocalDateTime): List<Event>
    fun addEvent(event: Event): Boolean
    fun updateEvent(event: Event): Boolean
    fun softDeleteEvent(eventId: Long): Boolean
}