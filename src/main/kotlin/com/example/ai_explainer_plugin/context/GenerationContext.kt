package com.example.ai_explainer_plugin.context

data class GenerationContext(
    val language: String?,
    val fileName: String?,
    val imports: String?,
    val enclosingClass: String?,
    val codeBeforeCursor: String,
    val codeAfterCursor: String,
    val userRequest: String,
)