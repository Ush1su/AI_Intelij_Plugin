package com.example.ai_explainer_plugin.ui

import com.intellij.openapi.Disposable
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import javax.swing.JEditorPane
import javax.swing.JPanel
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

object MarkdownRenderer {
    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()

    fun toHtml(markdown: String): String {
        val document = parser.parse(markdown)
        return renderer.render(document)
    }
}
class ExplainerPanel : Disposable {
    private val editorPane = JEditorPane().apply {
        contentType = "text/html"
        isEditable = false
    }

    val component: JPanel = JPanel(BorderLayout()).apply {
        add(JBScrollPane(editorPane), BorderLayout.CENTER)
    }

    fun showLoading() {
        editorPane.text = wrapHtml("<h3>Loading...</h3><p>Please wait...</p>")
        editorPane.caretPosition = 0
    }

    fun showExplanation(markdown: String) {
        val htmlBody = MarkdownRenderer.toHtml(markdown)
        editorPane.text = wrapHtml(htmlBody)
        editorPane.caretPosition = 0
    }

    fun showError(message: String) {
        editorPane.text = wrapHtml("<h3>Error</h3><p>${escapeHtml(message)}</p>")
        editorPane.caretPosition = 0
    }

    private fun wrapHtml(body: String): String {
        return """
            <html>
              <body style="font-family: sans-serif; padding: 12px;">
                $body
              </body>
            </html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
    }

    override fun dispose() {}
}