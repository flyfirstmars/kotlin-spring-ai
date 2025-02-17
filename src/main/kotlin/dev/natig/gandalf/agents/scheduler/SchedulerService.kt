package dev.natig.gandalf.agents.scheduler

import dev.natig.gandalf.agents.tools.DateTimeTools
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class SchedulerService(
    private val chatClient: ChatClient,
    private val schedulerTools: SchedulerTools,
    private val dateTimeTools: DateTimeTools
) {

    fun processRequest(
        payload: String
    ): String? {
        return chatClient.prompt()
            .system(SCHEDULER.SYSTEM_PROMPT)
            .user(payload)
            .tools(schedulerTools, dateTimeTools)
            .call()
            .content()
    }
}