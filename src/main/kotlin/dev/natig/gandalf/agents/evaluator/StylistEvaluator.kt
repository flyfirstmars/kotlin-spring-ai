package dev.natig.gandalf.agents.evaluator

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.ai.evaluation.EvaluationResponse
import org.springframework.ai.evaluation.Evaluator
import org.springframework.stereotype.Service

@Service
class StylistEvaluator(private val chatClient: ChatClient) : Evaluator {

    private val evaluatorPromptTemplate = """
        Evaluate the following stylist recommendation for relevance, clarity, and personalization.
        User Prompt: {userText}
        Stylist Response: {response}
        Provide your answer as a single-line JSON object with two fields:
        {"evaluation": "PASS" or "NEEDS_IMPROVEMENT", "feedback": "Detailed feedback if improvements are needed."}
    """.trimIndent()

    override fun evaluate(evaluationRequest: EvaluationRequest): EvaluationResponse {
        val prompt = evaluatorPromptTemplate
            .replace("{userText}", evaluationRequest.userText)
            .replace("{response}", evaluationRequest.responseContent)

        val evalOutput = chatClient.prompt()
            .system("You are an evaluator LLM for stylist recommendations.")
            .user(prompt)
            .call()
            .content().orEmpty()

        return if (evalOutput.contains("PASS", ignoreCase = true)) {
            EvaluationResponse(true, "The stylist response meets quality criteria.", emptyMap())
        } else {
            EvaluationResponse(false, "The stylist response needs more clarity or personalization.", emptyMap())
        }
    }
}