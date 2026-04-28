package com.example.ai_explainer_plugin.context.dto

/**
 * Context for the generation action. Built from the code before and after the cursor, the code metadata and the user request.
 */
data class GenerationContext(
    val metadata: CodeMetadata,
    val codeBeforeCursor: String,
    val codeAfterCursor: String,
    val userRequest: String,
)