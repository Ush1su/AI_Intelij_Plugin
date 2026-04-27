package com.example.ai_explainer_plugin.actions

import com.example.ai_explainer_plugin.context.dto.EditorContext
import com.example.ai_explainer_plugin.context.dto.ExplainContext
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.example.ai_explainer_plugin.context.extractors.EditorContextExtractor
import com.example.ai_explainer_plugin.context.extractors.ExplainContextExtractor
import com.example.ai_explainer_plugin.services.AiActionService
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.Logger

class ExplainSelectedTextAction : AnAction() {
    companion object {
        private val LOG = Logger.getInstance(ExplainSelectedTextAction::class.java)
    }
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val editor: Editor? = e.getData(CommonDataKeys.EDITOR)
        val hasSelection = editor?.selectionModel?.hasSelection() == true

        e.presentation.isEnabledAndVisible = hasSelection
    }

    override fun actionPerformed(e: AnActionEvent) {
        LOG.info("ExplainSelectedTextAction clicked")
        val editorContext = ReadAction.compute<EditorContext?, RuntimeException> {
            EditorContextExtractor.extract(e)
        } ?: return
        LOG.info("EditorContext extracted. selectedText: ${editorContext.selectedText?.length ?: 0} chars")
        val actionService = editorContext.project.service<AiActionService>()
        actionService.explain(editorContext)
        LOG.info("ExplainSelectedTextAction finished")
    }
}