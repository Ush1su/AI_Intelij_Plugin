package com.example.ai_explainer_plugin.services.prompts

/**
 * Data Transfer Object for LLM prompts that is used to send the prompt to the LLM.
 */
data class LLMPromptDTO(
    val systemPrompt: String,
    val userPrompt: String,
)