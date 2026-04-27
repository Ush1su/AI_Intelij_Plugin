package com.example.ai_explainer_plugin.actions

import com.example.ai_explainer_plugin.context.dto.EditorContext
import com.example.ai_explainer_plugin.context.extractors.EditorContextExtractor
import com.intellij.openapi.actionSystem.*
import com.example.ai_explainer_plugin.services.AiActionService
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger

class GenerateCodeAction : AnAction() {
    companion object {
        private val LOG = Logger.getInstance(AiActionService::class.java)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val hasEditor = e.getData(CommonDataKeys.EDITOR) != null
        e.presentation.isEnabledAndVisible = hasEditor
    }

    override fun actionPerformed(e: AnActionEvent) {
        LOG.info("GenerateCodeAction clicked")
        val editorContext = ReadAction.compute<EditorContext?, RuntimeException> {
            EditorContextExtractor.extract(e)
        } ?: return
        val actionService = editorContext.project.service<AiActionService>()
        actionService.generate(editorContext)
        LOG.info("GenerateCodeAction finished")
    }
}