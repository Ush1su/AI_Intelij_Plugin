package com.example.ai_explainer_plugin.context.extractors

import com.example.ai_explainer_plugin.context.dto.EditorContext
import com.intellij.psi.PsiClass
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil


object PsiCodeExtractor {
    fun getEnclosingMethodText(context: EditorContext): String? {
        val element = context.element ?: return null

        val javaMethod = PsiTreeUtil.getParentOfType(
            element,
            PsiMethod::class.java,
            false
        )

        val kotlinFunction = PsiTreeUtil.getParentOfType(
            element,
            KtNamedFunction::class.java,
            false
        )

        return javaMethod?.text ?: kotlinFunction?.text
    }

    fun getEnclosingClassText(context: EditorContext): String? {
        val element = context.element ?: return null

        val javaClass = PsiTreeUtil.getParentOfType(
            element,
            PsiClass::class.java,
            false
        )

        val kotlinClass = PsiTreeUtil.getParentOfType(
            element,
            KtClass::class.java,
            false
        )
        return javaClass?.text ?: kotlinClass?.text
    }

    fun getImports(context: EditorContext): List<String> {
        val psiFile = context.psiFile

        return when (psiFile) {
            is com.intellij.psi.PsiJavaFile ->
                psiFile.importList?.allImportStatements
                    ?.map { it.text }
                    ?: emptyList()

            is KtFile ->
                psiFile.importDirectives
                    .map { it.text }

            else -> emptyList()
        }
    }

    fun getImportsText(context: EditorContext): String? {
        val imports = getImports(context)
        return imports
            .takeIf { it.isNotEmpty() }
            ?.joinToString("\n")
    }

    fun getTextBeforeCursor(context: EditorContext) : String {
        val documentText = context.editor.document.text
        return documentText.take(context.offset)
    }

    fun getTextAfterCursor(context: EditorContext) : String {
        val documentText = context.editor.document.text
        return documentText.drop(context.offset)
    }
}