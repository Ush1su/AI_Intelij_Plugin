package com.example.ai_explainer_plugin.context.dto

data class GenerationContext(
    val metadata: CodeMetadata,
    val codeBeforeCursor: String,
    val codeAfterCursor: String,
    val userRequest: String,
)