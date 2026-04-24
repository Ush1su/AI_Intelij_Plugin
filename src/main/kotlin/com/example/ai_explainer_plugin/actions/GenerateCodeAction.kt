package com.example.ai_explainer_plugin.actions

import com.example.ai_explainer_plugin.context.EditorContextExtractor
import com.example.ai_explainer_plugin.context.GenerationContextBuilder
import com.example.ai_explainer_plugin.llm.LLMService
import com.example.ai_explainer_plugin.ui.CodePreviewDialog
import com.example.ai_explainer_plugin.ui.GenerateCodeDialog
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.EDT
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GenerateCodeAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val hasEditor = e.getData(CommonDataKeys.EDITOR) != null
        e.presentation.isEnabledAndVisible = hasEditor
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editorContext = EditorContextExtractor.extract(e) ?: return
        val editor = editorContext.editor ?: return

        val inputDialog = GenerateCodeDialog(project)
        if (!inputDialog.showAndGet()) return

        val userPrompt = inputDialog.getPrompt()
        if (userPrompt.isBlank()) return

        val llmService = project.service<LLMService>()
        val generationPrompt = GenerationContextBuilder.build(editorContext, userPrompt)
        val insertOffset = editor.caretModel.offset

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val generatedCode = llmService.generateCode(generationPrompt)

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
                    throw t
                }
            }
        }
    }
}