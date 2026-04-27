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
    private val onAccept: ((String) -> Unit)? = null,
) : DialogWrapper(project, true, IdeModalityType.MODELESS) {

    private val codeArea = JBTextArea("Generating code...", 18, 70).apply {
        lineWrap = false
        isEditable = false
    }

    init {
        title = "Preview Generated Code"
        setOKButtonText("Accept")
        setCancelButtonText("Discard")
        init()
        isOKActionEnabled = false
    }

    override fun createCenterPanel(): JComponent {
        return JPanel(BorderLayout()).apply {
            add(JBScrollPane(codeArea), BorderLayout.CENTER)
        }
    }

    fun showGeneratedCode(code: String) {
        codeArea.text = code
        codeArea.caretPosition = 0
        codeArea.isEditable = true
        isOKActionEnabled = true
    }

    fun showError(message: String) {
        codeArea.text = "Generation failed:\n\n$message"
    }

    fun getCode(): String = codeArea.text

    override fun doOKAction() {
        val acceptedCode = getCode()

        onAccept?.invoke(acceptedCode)

        super.doOKAction()
    }
}