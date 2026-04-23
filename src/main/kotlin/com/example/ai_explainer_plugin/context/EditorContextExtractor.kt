package com.example.ai_explainer_plugin.context

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

object EditorContextExtractor {
    fun extract(e: AnActionEvent): EditorContext? {
        val project = e.project ?: return null
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)

        val offset = if (editor?.selectionModel?.hasSelection() == true) {
            editor.selectionModel.selectionStart
        } else {
            editor?.caretModel?.offset
        }

        return EditorContext(
            project = project,
            editor = editor,
            psiFile = psiFile,
            selectedText = editor?.selectionModel?.selectedText,
            offset = offset,
        )
    }
}