package com.example.ai_explainer_plugin.context

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil

object PsiCodeExtractor {

    fun getFile(context: EditorContext): String? =
        context.psiFile?.text

    fun getSelectedText(context: EditorContext): String? =
        context.selectedText

    fun getElementAtContext(context: EditorContext): PsiElement? {
        val psiFile = context.psiFile ?: return null
        val offset = context.offset ?: return null
        return psiFile.findElementAt(offset)
    }

    fun getEnclosingMethod(context: EditorContext): PsiMethod? {
        val element = getElementAtContext(context) ?: return null
        return PsiTreeUtil.getParentOfType(element, PsiMethod::class.java, false)
    }

    fun getEnclosingClass(context: EditorContext): PsiClass? {
        val element = getElementAtContext(context) ?: return null
        return PsiTreeUtil.getParentOfType(element, PsiClass::class.java, false)
    }

    fun getEnclosingMethodText(context: EditorContext): String? =
        getEnclosingMethod(context)?.text

    fun getEnclosingClassText(context: EditorContext): String? =
        getEnclosingClass(context)?.text
}