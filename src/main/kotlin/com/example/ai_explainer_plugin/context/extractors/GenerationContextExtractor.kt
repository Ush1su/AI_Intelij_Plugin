package com.example.ai_explainer_plugin.context.extractors

import com.example.ai_explainer_plugin.context.dto.EditorContext
import com.example.ai_explainer_plugin.context.dto.GenerationContext

object GenerationContextExtractor {
    private const val BEFORE_CURSOR_LIMIT = 2_000
    private const val AFTER_CURSOR_LIMIT = 2_000
    fun extract(
        editorContext: EditorContext,
        userRequest: String,
    ): GenerationContext {
        val beforeCursor = PsiCodeExtractor.getTextBeforeCursor(editorContext)
            .takeLast(BEFORE_CURSOR_LIMIT)
        val afterCursor = PsiCodeExtractor.getTextAfterCursor(editorContext)
            .take(AFTER_CURSOR_LIMIT)
        val codeMetadata = CodeMetadataExtractor.extract(editorContext)
        return GenerationContext(
            metadata = codeMetadata,
            codeBeforeCursor = beforeCursor,
            codeAfterCursor = afterCursor,
            userRequest = userRequest,
        )
    }
}