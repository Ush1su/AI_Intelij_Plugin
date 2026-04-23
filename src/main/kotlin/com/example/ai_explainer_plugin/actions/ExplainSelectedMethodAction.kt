package com.example.ai_explainer_plugin.actions

import com.intellij.openapi.actionSystem.*
import com.example.ai_explainer_plugin.context.PsiCodeExtractor
import com.example.ai_explainer_plugin.context.EditorContextExtractor

class ExplainSelectedMethodAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val editorContext = EditorContextExtractor.extract(e)
        val visible = editorContext != null && PsiCodeExtractor.getEnclosingMethod(editorContext) != null
        e.presentation.isEnabledAndVisible = visible
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editorContext = EditorContextExtractor.extract(e) ?: return
        val method = PsiCodeExtractor.getEnclosingMethod(editorContext) ?: return

    }
}