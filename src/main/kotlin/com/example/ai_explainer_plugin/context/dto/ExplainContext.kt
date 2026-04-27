package com.example.ai_explainer_plugin.context.dto

data class ExplainContext(
    val metadata: CodeMetadata,
    val selectedText: String,
)