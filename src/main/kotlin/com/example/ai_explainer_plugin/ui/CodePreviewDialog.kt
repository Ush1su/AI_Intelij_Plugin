package com.example.ai_explainer_plugin.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class CodePreviewDialog(
    project: Project,
    generatedCode: String,
) : DialogWrapper(project) {
    private val codeArea = JBTextArea(generatedCode, 18, 70).apply {
        lineWrap = false
        isEditable = true
    }

    init {
        title = "Preview Generated Code"
        setOKButtonText("Accept")
        setCancelButtonText("Discard")
        init()
    }

    override fun createCenterPanel(): JComponent {
        return JPanel(BorderLayout()).apply {
            add(JBScrollPane(codeArea), BorderLayout.CENTER)
        }
    }

    fun getCode(): String = codeArea.text
}