package com.example.ai_explainer_plugin.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.vfs.VirtualFile

class ExplainFileAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        val file: VirtualFile? = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = file != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val project = e.project ?: return

        println("Explain file: ${file.path}")
    }
}