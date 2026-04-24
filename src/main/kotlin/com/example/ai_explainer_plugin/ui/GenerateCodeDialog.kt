package com.example.ai_explainer_plugin.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class GenerateCodeDialog(project: Project) : DialogWrapper(project) {
    private val promptArea = JBTextArea(8, 50).apply {
        lineWrap = true
        wrapStyleWord = true
        emptyText.text = "Describe what code should be generated..."
    }

    init {
        title = "Generate Code"
        init()
    }

    override fun createCenterPanel(): JComponent {
        return JPanel(BorderLayout()).apply {
            add(promptArea, BorderLayout.CENTER)
        }
    }

    fun getPrompt(): String = promptArea.text.trim()
}