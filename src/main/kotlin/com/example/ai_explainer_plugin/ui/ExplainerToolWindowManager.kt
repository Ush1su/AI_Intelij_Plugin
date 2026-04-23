package com.example.ai_explainer_plugin.ui

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory

@Service(Service.Level.PROJECT)
class ExplainerToolWindowManager(private val project: Project) {

    private var counter = 1

    fun createResultTab(title: String = "Explanation ${counter++}"): ExplainerResultHandle? {
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Explainer")
            ?: return null

        val panel = ExplainerPanel()
        val content = ContentFactory.getInstance()
            .createContent(panel.component, title, false)

        content.isCloseable = true
        content.setDisposer(panel)

        toolWindow.contentManager.addContent(content)
        toolWindow.contentManager.setSelectedContent(content)
        toolWindow.activate(null)

        return ExplainerResultHandle(panel, content)
    }
}

class ExplainerResultHandle(
    private val panel: ExplainerPanel,
    private val content: Content,
) {
    fun showLoading() {
        panel.showLoading()
    }

    fun showExplanation(text: String) {
        panel.showExplanation(text)
    }

    fun showError(message: String) {
        panel.showError(message)
    }

    fun setTitle(title: String) {
        content.displayName = title
    }
}