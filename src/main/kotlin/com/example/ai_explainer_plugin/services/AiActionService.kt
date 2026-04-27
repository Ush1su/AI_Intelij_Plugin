package com.example.ai_explainer_plugin.services

import com.example.ai_explainer_plugin.context.dto.EditorContext
import com.example.ai_explainer_plugin.context.dto.ExplainContext
import com.example.ai_explainer_plugin.context.extractors.ExplainContextExtractor
import com.example.ai_explainer_plugin.context.extractors.GenerationContextExtractor
import com.example.ai_explainer_plugin.ui.CodePreviewDialog
import com.example.ai_explainer_plugin.ui.ExplainerToolWindowManager
import com.example.ai_explainer_plugin.ui.GenerateCodeDialog
import com.intellij.openapi.application.EDT
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Service(Service.Level.PROJECT)
class AiActionService(
    private val project: Project,
    private val scope: CoroutineScope,
) {
    private val llmService: LLMService
        get() = project.service()

    companion object {
        private val LOG = Logger.getInstance(AiActionService::class.java)
    }
    fun generate(editorContext: EditorContext) {
        val inputDialog = GenerateCodeDialog(project)
        if (!inputDialog.showAndGet()) return

        val userPrompt = inputDialog.getPrompt()
        if (userPrompt.isBlank()) return

        val generationContext = GenerationContextExtractor.extract(editorContext, userPrompt)
        val insertOffset = editorContext.offset
        val editor = editorContext.editor
        LOG.info("GenerationContext extracted: $generationContext")
        scope.launch {
            try {
                val generatedCode = llmService.generateCode(generationContext)

                withContext(Dispatchers.EDT) {
                    val previewDialog = CodePreviewDialog(project, generatedCode)
                    if (!previewDialog.showAndGet()) return@withContext

                    val acceptedCode = previewDialog.getCode()

                    WriteCommandAction.runWriteCommandAction(project) {
                        editor.document.insertString(insertOffset, acceptedCode)
                    }
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.EDT) {
                    LOG.error("Generation failed: ${t.message}")
                }
            }
        }
    }
    fun explain(editorContext: EditorContext) {
        val toolWindowManager = project.service<ExplainerToolWindowManager>()

        val resultTab = toolWindowManager.createResultTab() ?: return
        resultTab.showLoading()
        val explainContext = ReadAction.compute<ExplainContext?, RuntimeException> {
            ExplainContextExtractor.extract(editorContext)
        }
        LOG.info("ExplainContext extracted: $explainContext")
        scope.launch {
            try {
                val explanation = llmService.explainCode(explainContext!!)

                withContext(Dispatchers.EDT) {
                    resultTab.showExplanation(explanation)
                }
            } catch (t: Throwable) {
                LOG.error("Explanation failed: ${t.message}")
                withContext(Dispatchers.EDT) {
                    resultTab.showError(t.message ?: "Explanation failed")
                }
            }
        }
    }
}