package com.example.ai_explainer_plugin.context.dto

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * Base context of the editor that is used to extract information from IDE for Explain and Generation actions.
 */
data class EditorContext(
    val project: Project,
    val editor: Editor,
    val psiFile: PsiFile,
    val selectedText: String?,
    val offset: Int,
    val element: PsiElement?,
)