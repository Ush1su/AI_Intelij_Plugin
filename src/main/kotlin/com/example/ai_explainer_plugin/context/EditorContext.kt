package com.example.ai_explainer_plugin.context

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

data class EditorContext(
    val project: Project,
    val editor: Editor?,
    val psiFile: PsiFile?,
    val selectedText: String?,
    val offset: Int?,
)