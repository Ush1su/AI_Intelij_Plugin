package com.example.ai_explainer_plugin.context.extractors

import com.example.ai_explainer_plugin.context.dto.EditorContext
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ReadAction

object EditorContextExtractor {
    fun extract(e: AnActionEvent): EditorContext? {
        val project = e.project ?: return null
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return null
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return null

        val offset = if (editor.selectionModel.hasSelection()) {
            editor.selectionModel.selectionStart
        } else {
            editor.caretModel.offset
        }
        val element_ = ReadAction.compute<_, RuntimeException> {
            psiFile.findElementAt(offset)
        }

        return EditorContext(
            project = project,
            editor = editor,
            psiFile = psiFile,
            selectedText = editor.selectionModel.selectedText,
            offset = offset,
            element = element_,
        )
    }
}