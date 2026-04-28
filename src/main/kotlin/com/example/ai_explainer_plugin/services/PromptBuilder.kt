package com.example.ai_explainer_plugin.services

import com.example.ai_explainer_plugin.context.dto.GenerationContext
import com.example.ai_explainer_plugin.context.dto.ExplainContext
import com.example.ai_explainer_plugin.services.prompts.LLMPromptDTO
import com.example.ai_explainer_plugin.services.prompts.EXPLAIN_PROMPT
import com.example.ai_explainer_plugin.services.prompts.GENERATE_PROMPT

/**
 * Builds the prompt for the LLM based on the context.
 */
object PromptBuilder {

    fun buildExplainPrompt(context: ExplainContext): LLMPromptDTO {
        val userPrompt = """
            Language: ${context.metadata.language ?: "Unknown"}
            File: ${context.metadata.fileName ?: "Unknown"}

            Imports:
            ${context.metadata.imports ?: "No imports found"}

            Enclosing class:
            ${context.metadata.enclosingClass ?: "No enclosing class found"}

            Code that you must explain:
            ${context.selectedText}
            ```
        """.trimIndent()
        return LLMPromptDTO(
            systemPrompt = EXPLAIN_PROMPT,
            userPrompt = userPrompt
        )
    }

    fun buildGenerationPrompt(context: GenerationContext): LLMPromptDTO {
        val userPrompt = """
            Language: ${context.metadata.language ?: "Unknown"}
            File: ${context.metadata.fileName ?: "Unknown"}

            Imports:
            ${context.metadata.imports ?: "No imports found"}

            Enclosing class:
            ${context.metadata.enclosingClass ?: "No enclosing class found"}

            Code before cursor:
            ```
            ${context.codeBeforeCursor}
            ```

            Code after cursor:
            ```
            ${context.codeAfterCursor}
            ```

            User request:
            ${context.userRequest}
        """.trimIndent()
        return LLMPromptDTO(
            systemPrompt = GENERATE_PROMPT,
            userPrompt = userPrompt
        )
    }
}