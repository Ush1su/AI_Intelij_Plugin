package com.example.ai_explainer_plugin.context.extractors

import com.example.ai_explainer_plugin.context.dto.EditorContext
import com.example.ai_explainer_plugin.context.dto.CodeMetadata
import com.example.ai_explainer_plugin.context.extractors.PsiCodeExtractor

object CodeMetadataExtractor {
    fun extract(context: EditorContext): CodeMetadata {
        return CodeMetadata(
            language = context.psiFile.language.displayName,
            fileName = context.psiFile.name,
            imports = PsiCodeExtractor.getImportsText(context),
            enclosingClass = PsiCodeExtractor.getEnclosingClassText(context),
            enclosingMethod = PsiCodeExtractor.getEnclosingMethodText(context),
        )
    }
}