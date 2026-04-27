package com.example.ai_explainer_plugin.services.prompts

data class LLMPromptDTO(
    val systemPrompt: String,
    val userPrompt: String,
)