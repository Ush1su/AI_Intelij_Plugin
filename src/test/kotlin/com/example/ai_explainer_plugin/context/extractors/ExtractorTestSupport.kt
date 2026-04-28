package com.example.ai_explainer_plugin.context.extractors

import com.example.ai_explainer_plugin.context.dto.EditorContext
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.CodeInsightTestFixture

internal fun CodeInsightTestFixture.editorContextFromFixture(
    psiFile: PsiFile,
    selectedText: String? = editor.selectionModel.selectedText,
    offset: Int = if (editor.selectionModel.hasSelection()) {
        editor.selectionModel.selectionStart
    } else {
        editor.caretModel.offset
    },
): EditorContext {
    return EditorContext(
        project = project,
        editor = editor,
        psiFile = psiFile,
        selectedText = selectedText,
        offset = offset,
        element = psiFile.findElementAt(offset),
    )
}
