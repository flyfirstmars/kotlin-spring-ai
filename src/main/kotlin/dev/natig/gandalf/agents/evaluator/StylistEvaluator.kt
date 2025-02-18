package dev.natig.gandalf.agents.evaluator

import dev.natig.gandalf.common.prompt
import dev.natig.gandalf.common.applyCommonRules
import kotlinx.serialization.Serializable
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.ai.evaluation.EvaluationResponse
import org.springframework.ai.evaluation.Evaluator
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.stereotype.Service

@Service
class StylistEvaluator(private val chatClient: ChatClient) : Evaluator {

    private val evaluatorPrompt: String = prompt {
        role("You are an evaluator LLM for stylist recommendations.")
        goal("Evaluate stylist recommendations for relevance, clarity, and personalization.")
        tone("Professional and objective")
        languageStyle("Use clear and precise language.")
        fallbackStrategy("If the recommendation does not meet the guidelines, provide detailed feedback for improvement.")
        ensure("The stylist recommendation must provide personalized, clear, and actionable clothing and style suggestions based on the user's prompt.")
        applyCommonRules(this)
        focusArea("Relevance & Personalization")
        focusArea("Alignment with Role")
        focusArea("Appropriateness and Clarity")
        clarify("Verify that the recommendation is actionable and tailored; if not, it needs improvement.")
        reflect("Before returning your evaluation, ensure that all guidelines are met.")
        ensure("User Prompt: {userText}\nStylist Response: {response}\nReturn your answer as a single-line JSON object: {{ \"evaluation\": \"PASS\", \"feedback\": \"Detailed feedback if improvements are needed.\" }}")
    }

    override fun evaluate(evaluationRequest: EvaluationRequest): EvaluationResponse {
        val promptToSend = evaluatorPrompt
            .replace("{userText}", evaluationRequest.userText)
            .replace("{response}", evaluationRequest.responseContent)

        val converter = BeanOutputConverter(StylistEvaluationResponse::class.java)
        val generation = chatClient.prompt()
            .system("You are an evaluator LLM for stylist recommendations.")
            .user(promptToSend)
            .call()
        val output = generation.content().orEmpty()
        val structuredEval = converter.convert(output)

        return if (structuredEval?.evaluation == EvaluationStatus.PASS) {
            EvaluationResponse(true, structuredEval.feedback, emptyMap())
        } else {
            EvaluationResponse(
                false,
                structuredEval?.feedback ?: "Evaluator did not return structured output.",
                emptyMap()
            )
        }
    }

    @Serializable
    enum class EvaluationStatus {
        PASS
    }

    @Serializable
    data class StylistEvaluationResponse(
        val evaluation: EvaluationStatus,
        val feedback: String
    )
}