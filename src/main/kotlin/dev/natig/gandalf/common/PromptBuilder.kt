package dev.natig.gandalf.common

fun prompt(init: PromptBuilder.() -> Unit): String {
    val builder = PromptBuilder()
    builder.init()
    return builder.build()
}

class PromptBuilder {
    private val sections = mutableListOf<String>()

    fun role(description: String) {
        sections.add("Role: $description")
    }

    fun goal(description: String) {
        sections.add("Goal: $description")
    }

    fun tone(description: String) {
        sections.add("Tone: $description")
    }

    fun languageStyle(description: String) {
        sections.add("Language Style: $description")
    }

    fun fallbackStrategy(description: String) {
        sections.add("Fallback Strategy: $description")
    }

    fun ensure(description: String) {
        sections.add("Ensure: $description")
    }

    fun focusArea(description: String) {
        sections.add("Focus Area: $description")
    }

    fun example(init: ExampleBuilder.() -> Unit) {
        val builder = ExampleBuilder()
        builder.init()
        sections.add("Example:\n" + builder.build())
    }

    fun restrictedTopics(vararg topics: String) {
        sections.add("Restricted Topics: ${topics.joinToString(", ")}")
    }

    fun avoid(description: String) {
        sections.add("Avoid: $description")
    }

    fun clarify(description: String) {
        sections.add("Clarify: $description")
    }

    fun reflect(description: String) {
        sections.add("Reflect: $description")
    }

    fun build(): String = sections.joinToString("\n\n")
}

class ExampleBuilder {
    private val parts = mutableListOf<String>()

    fun user(message: String) {
        parts.add("User: \"$message\"")
    }

    fun styleBot(response: String) {
        parts.add("StyleBot: \"$response\"")
    }

    fun build(): String = parts.joinToString("\n")
}