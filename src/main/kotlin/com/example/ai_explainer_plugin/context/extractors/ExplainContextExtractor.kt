package com.example.ai_explainer_plugin.context.extractors

import com.example.ai_explainer_plugin.context.dto.EditorContext
import com.example.ai_explainer_plugin.context.dto.ExplainContext
import com.example.ai_explainer_plugin.context.extractors.PsiCodeExtractor

/**
 * Extracts the ExplainContext from the EditorContext.
 */
object ExplainContextExtractor {

    fun extract(context: EditorContext): ExplainContext {
        return ExplainContext(
            metadata = CodeMetadataExtractor.extract(context),
            selectedText = context.selectedText ?: "",
        )
    }
}