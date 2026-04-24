package com.example.ai_explainer_plugin.llm

import com.example.ai_explainer_plugin.context.GenerationContext
import com.example.ai_explainer_plugin.llm.prompts.LLMPromptDTO
import com.example.ai_explainer_plugin.llm.prompts.EXPLAIN_PROMPT
import com.example.ai_explainer_plugin.llm.prompts.GENERATE_PROMPT

object PromptBuilder {

    fun buildExplainPrompt(content: String): LLMPromptDTO {
        val userPrompt = """
            Explain the following content:

            ```
            $content
            ```
        """.trimIndent()
        return LLMPromptDTO(
            systemPrompt = EXPLAIN_PROMPT,
            userPrompt = userPrompt
        )
    }

    fun buildGenerationPrompt(context: GenerationContext): LLMPromptDTO {
        val userPrompt = """
            Language: ${context.language ?: "Unknown"}
            File: ${context.fileName ?: "Unknown"}

            Imports:
            ${context.imports ?: "No imports found"}

            Enclosing class:
            ${context.enclosingClass ?: "No enclosing class found"}

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