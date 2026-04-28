package com.example.ai_explainer_plugin.context.dto

/**
 * Context of the editor for Explain action. Built from the selected text in the editor and the code metadata.
 */
data class ExplainContext(
    val metadata: CodeMetadata,
    val selectedText: String,
)