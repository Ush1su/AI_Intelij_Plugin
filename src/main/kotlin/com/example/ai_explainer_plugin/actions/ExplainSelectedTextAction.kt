package com.example.ai_explainer_plugin.actions

import com.example.ai_explainer_plugin.services.llm.LLMService
import com.example.ai_explainer_plugin.ui.ExplainerToolWindowManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.ai_explainer_plugin.context.PsiCodeExtractor
import com.example.ai_explainer_plugin.context.EditorContextExtractor

class ExplainSelectedTextAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        val hasSelection = editor?.selectionModel?.hasSelection() == true

        e.presentation.isEnabledAndVisible = hasSelection
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editorContext = EditorContextExtractor.extract(e) ?: return
        val selectedText = PsiCodeExtractor.getSelectedText(editorContext) ?: return

        val llmService = project.service<LLMService>()
        val toolWindowManager = project.service<ExplainerToolWindowManager>()

        val resultTab = toolWindowManager.createResultTab() ?: return
        resultTab.showLoading()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val explanation = llmService.explain(selectedText)

                withContext(Dispatchers.EDT) {
                    resultTab.showExplanation(explanation)
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.EDT) {
                    resultTab.showError(t.message ?: "Unknown error")
                }
            }
        }
    }
}