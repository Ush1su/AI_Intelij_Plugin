package com.example.ai_explainer_plugin.context.dto

data class CodeMetadata(
    val language: String?,
    val fileName: String?,
    val imports: String?,
    val enclosingClass: String?,
    val enclosingMethod: String?,
)