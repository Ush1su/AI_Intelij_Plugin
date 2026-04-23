package com.example.ai_explainer_plugin.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightVirtualFile

object ExplanationFileOpener {

    fun openMarkdown(project: Project, markdown: String) {
        val virtualFile = LightVirtualFile("Explanation.md", markdown)

        ApplicationManager.getApplication().invokeLater {
            FileEditorManager.getInstance(project).openFile(virtualFile, true)
        }
    }
}