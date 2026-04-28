package com.example.ai_explainer_plugin.context.extractors

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert.*

class GenerationContextExtractorTest : BasePlatformTestCase() {

    fun testGenerationContextIncludesUserRequestMetadataAndCursorSurroundings() {
        val psiFile = myFixture.configureByText(
            "Calculator.kt",
            """
                package demo

                import kotlin.math.max

                class Calculator {
                    fun sum(a: Int, b: Int): Int {
                        return a <caret>+ b
                    }
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val result = GenerationContextExtractor.extract(context, "Add validation")

        assertEquals("Add validation", result.userRequest)
        assertEquals("Kotlin", result.metadata.language)
        assertEquals("Calculator.kt", result.metadata.fileName)
        assertTrue(result.metadata.imports!!.contains("import kotlin.math.max"))
        assertTrue(result.metadata.enclosingClass!!.contains("class Calculator"))
        assertTrue(result.metadata.enclosingMethod!!.contains("fun sum"))
        assertTrue(result.codeBeforeCursor.endsWith("return a "))
        assertTrue(result.codeAfterCursor.startsWith("+ b"))
    }

    fun testBeforeCursorTextIsCappedToLast2000Characters() {
        val oldPrefix = "x".repeat(800)
        val expectedTail = "a".repeat(2_000)
        val psiFile = myFixture.configureByText(
            "LongBefore.kt",
            oldPrefix + expectedTail + "<caret>" + "after"
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val result = GenerationContextExtractor.extract(context, "Generate")

        assertEquals(2_000, result.codeBeforeCursor.length)
        assertEquals(expectedTail, result.codeBeforeCursor)
        assertFalse(result.codeBeforeCursor.contains("x"))
    }

    fun testAfterCursorTextIsCappedToFirst2000Characters() {
        val expectedHead = "b".repeat(2_000)
        val removedTail = "y".repeat(800)
        val psiFile = myFixture.configureByText(
            "LongAfter.kt",
            "before" + "<caret>" + expectedHead + removedTail
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val result = GenerationContextExtractor.extract(context, "Generate")

        assertEquals(2_000, result.codeAfterCursor.length)
        assertEquals(expectedHead, result.codeAfterCursor)
        assertFalse(result.codeAfterCursor.contains("y"))
    }

    fun testCursorAtStartGivesEmptyBeforeContext() {
        val psiFile = myFixture.configureByText(
            "StartGeneration.kt",
            "<caret>fun main() = println(1)"
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val result = GenerationContextExtractor.extract(context, "Generate")

        assertEquals("", result.codeBeforeCursor)
        assertEquals("fun main() = println(1)", result.codeAfterCursor)
    }

    fun testCursorAtEndGivesEmptyAfterContext() {
        val code = "fun main() = println(1)"
        val psiFile = myFixture.configureByText("EndGeneration.kt", "$code<caret>")
        val context = myFixture.editorContextFromFixture(psiFile)

        val result = GenerationContextExtractor.extract(context, "Generate")

        assertEquals(code, result.codeBeforeCursor)
        assertEquals("", result.codeAfterCursor)
    }

    fun testNormalCodeProducesNonEmptyContextAroundCursor() {
        val psiFile = myFixture.configureByText(
            "Normal.kt",
            "class A { fun f() { val x = <caret>1 } }"
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val result = GenerationContextExtractor.extract(context, "Generate")

        assertTrue(result.codeBeforeCursor.isNotEmpty())
        assertTrue(result.codeAfterCursor.isNotEmpty())
    }

    fun testEmptyFileStillCreatesGenerationContext() {
        val psiFile = myFixture.configureByText("EmptyGeneration.kt", "<caret>")
        val context = myFixture.editorContextFromFixture(psiFile)

        val result = GenerationContextExtractor.extract(context, "Generate")

        assertEquals("", result.codeBeforeCursor)
        assertEquals("", result.codeAfterCursor)
        assertEquals("Generate", result.userRequest)
        assertEquals("EmptyGeneration.kt", result.metadata.fileName)
        assertNull(result.metadata.enclosingClass)
        assertNull(result.metadata.enclosingMethod)
    }
}
