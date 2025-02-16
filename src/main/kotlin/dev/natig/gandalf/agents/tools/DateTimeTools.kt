package dev.natig.gandalf.agents.tools

import kotlinx.datetime.Clock
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component

@Component
class DateTimeTools {

    @Tool(description = "Get the current date and time in UTC", returnDirect = true)
    fun getCurrentDateTime(): String {
        return Clock.System.now().toString()
    }
}