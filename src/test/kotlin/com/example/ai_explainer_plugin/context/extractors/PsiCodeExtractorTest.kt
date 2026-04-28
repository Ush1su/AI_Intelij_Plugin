package com.example.ai_explainer_plugin.context.extractors

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert.*

class PsiCodeExtractorTest : BasePlatformTestCase() {

    fun testCursorAtStartReturnsEmptyBeforeTextAndFullAfterText() {
        val psiFile = myFixture.configureByText(
            "Start.kt",
            "<caret>class A { fun f() = 1 }"
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        assertEquals("", PsiCodeExtractor.getTextBeforeCursor(context))
        assertEquals("class A { fun f() = 1 }", PsiCodeExtractor.getTextAfterCursor(context))
    }

    fun testCursorAtEndReturnsFullBeforeTextAndEmptyAfterText() {
        val code = "class A { fun f() = 1 }"
        val psiFile = myFixture.configureByText("End.kt", "$code<caret>")
        val context = myFixture.editorContextFromFixture(psiFile)

        assertEquals(code, PsiCodeExtractor.getTextBeforeCursor(context))
        assertEquals("", PsiCodeExtractor.getTextAfterCursor(context))
    }

    fun testSelectedTextIsAvailableThroughExplainContext() {
        val psiFile = myFixture.configureByText(
            "Selection.kt",
            """
                class Greeter {
                    fun greet() {
                        <selection>println("Hello")</selection>
                    }
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val result = ExplainContextExtractor.extract(context)

        assertEquals("println(\"Hello\")", result.selectedText.trim())
    }

    fun testNoSelectedTextBecomesEmptyStringInExplainContext() {
        val psiFile = myFixture.configureByText(
            "NoSelection.kt",
            "class A { fun f() { <caret>println(1) } }"
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val result = ExplainContextExtractor.extract(context)

        assertEquals("", result.selectedText)
    }

    fun testKotlinImportsAreExtracted() {
        val psiFile = myFixture.configureByText(
            "Imports.kt",
            """
                package demo

                import kotlin.math.max
                import kotlin.math.min

                fun main() { <caret>println(max(1, min(2, 3))) }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val imports = PsiCodeExtractor.getImports(context)

        assertEquals(listOf("import kotlin.math.max", "import kotlin.math.min"), imports)
        assertEquals("import kotlin.math.max\nimport kotlin.math.min", PsiCodeExtractor.getImportsText(context))
    }

    fun testUnsupportedTextFileHasNoImportsClassOrMethod() {
        val psiFile = myFixture.configureByText(
            "notes.txt",
            "some random text <caret> without code"
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        assertTrue(PsiCodeExtractor.getImports(context).isEmpty())
        assertNull(PsiCodeExtractor.getImportsText(context))
        assertNull(PsiCodeExtractor.getEnclosingClassText(context))
        assertNull(PsiCodeExtractor.getEnclosingMethodText(context))
    }

    fun testEmptyFileDoesNotCrashAndReturnsEmptyContext() {
        val psiFile = myFixture.configureByText("Empty.kt", "<caret>")
        val context = myFixture.editorContextFromFixture(psiFile)

        assertEquals("", PsiCodeExtractor.getTextBeforeCursor(context))
        assertEquals("", PsiCodeExtractor.getTextAfterCursor(context))
        assertNull(PsiCodeExtractor.getEnclosingClassText(context))
        assertNull(PsiCodeExtractor.getEnclosingMethodText(context))
        assertTrue(PsiCodeExtractor.getImports(context).isEmpty())
    }

    fun testFileWithClassButCursorOutsideMethodHasClassAndNoMethod() {
        val psiFile = myFixture.configureByText(
            "ClassOnly.kt",
            """
                class User {
                    val nam<caret>e: String = "Ann"
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        assertTrue(PsiCodeExtractor.getEnclosingClassText(context)!!.contains("class User"))
        assertNull(PsiCodeExtractor.getEnclosingMethodText(context))
    }

    fun testNestedClassResolvesNearestEnclosingClass() {
        val psiFile = myFixture.configureByText(
            "Nested.kt",
            """
                class Outer {
                    class Inner {
                        fun run() {
                            print<caret>ln("x")
                        }
                    }
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val enclosingClass = PsiCodeExtractor.getEnclosingClassText(context)!!
        val enclosingMethod = PsiCodeExtractor.getEnclosingMethodText(context)!!

        assertTrue(enclosingClass.contains("class Inner"))
        assertFalse("Should return nearest class, not the outer class text", enclosingClass.startsWith("class Outer"))
        assertTrue(enclosingMethod.contains("fun run"))
    }

    fun testToplevelKotlinFunctionHasMethodButNoClass() {
        val psiFile = myFixture.configureByText(
            "TopLevel.kt",
            """
                fun helper(value: Int): Int {
                    return val<caret>ue + 1
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        assertNull(PsiCodeExtractor.getEnclosingClassText(context))
        assertTrue(PsiCodeExtractor.getEnclosingMethodText(context)!!.contains("fun helper"))
    }

    fun testJavaMethodAndClassAreExtracted() {
        val psiFile = myFixture.configureByText(
            "Calculator.java",
            """
                package demo;

                import java.util.List;

                public class Calculator {
                    public int sum(int a, int b) {
                        return a +<caret> b;
                    }
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        assertEquals(listOf("import java.util.List;"), PsiCodeExtractor.getImports(context))
        assertTrue(PsiCodeExtractor.getEnclosingClassText(context)!!.contains("class Calculator"))
        assertTrue(PsiCodeExtractor.getEnclosingMethodText(context)!!.contains("int sum"))
    }

    fun testBrokenKotlinCodeDoesNotCrash() {
        val psiFile = myFixture.configureByText(
            "Broken.kt",
            """
                class Broken {
                    fun test() {
                        if (<caret>
                    }
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        assertNotNull(PsiCodeExtractor.getEnclosingClassText(context))
        assertNotNull(PsiCodeExtractor.getEnclosingMethodText(context))
    }
}
