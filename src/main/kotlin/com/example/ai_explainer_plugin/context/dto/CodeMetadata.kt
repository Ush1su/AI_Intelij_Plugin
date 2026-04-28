package com.example.ai_explainer_plugin.context.dto

/**
 * Metadata about the code for LLM to understand the context for latter explanation or generation.
 */
data class CodeMetadata(
    val language: String?,
    val fileName: String?,
    val imports: String?,
    val enclosingClass: String?,
    val enclosingMethod: String?,
)