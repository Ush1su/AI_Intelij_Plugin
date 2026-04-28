package com.example.ai_explainer_plugin.context.extractors

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert.*

class CodeMetadataExtractorTest : BasePlatformTestCase() {

    fun testMetadataDetectsKotlinLanguageAndFileName() {
        val psiFile = myFixture.configureByText(
            "Main.kt",
            "fun main() { <caret>println(1) }"
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val metadata = CodeMetadataExtractor.extract(context)

        assertEquals("Kotlin", metadata.language)
        assertEquals("Main.kt", metadata.fileName)
    }

    fun testMetadataDetectsEnclosingKotlinClassAndMethod() {
        val psiFile = myFixture.configureByText(
            "Greeter.kt",
            """
                class Greeter {
                    fun greet(name: String) {
                        println("Hello, ${'$'}na<caret>me")
                    }
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val metadata = CodeMetadataExtractor.extract(context)

        assertTrue(metadata.enclosingClass!!.contains("class Greeter"))
        assertTrue(metadata.enclosingMethod!!.contains("fun greet"))
    }

    fun testMetadataHasClassButNullMethodWhenCursorIsInProperty() {
        val psiFile = myFixture.configureByText(
            "User.kt",
            """
                class User {
                    val ag<caret>e = 20
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val metadata = CodeMetadataExtractor.extract(context)

        assertTrue(metadata.enclosingClass!!.contains("class User"))
        assertNull(metadata.enclosingMethod)
    }

    fun testMetadataForTopLevelFunctionHasMethodButNullClass() {
        val psiFile = myFixture.configureByText(
            "Utils.kt",
            """
                fun normalize(input: String): String {
                    return input.tr<caret>im()
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val metadata = CodeMetadataExtractor.extract(context)

        assertNull(metadata.enclosingClass)
        assertTrue(metadata.enclosingMethod!!.contains("fun normalize"))
    }

    fun testMetadataDetectsJavaFileClassMethodAndImports() {
        val psiFile = myFixture.configureByText(
            "Service.java",
            """
                import java.util.ArrayList;
                import java.util.List;

                public class Service {
                    public List<String> names() {
                        return new Array<caret>List<>();
                    }
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val metadata = CodeMetadataExtractor.extract(context)

        assertEquals("Java", metadata.language)
        assertEquals("Service.java", metadata.fileName)
        assertEquals("import java.util.ArrayList;\nimport java.util.List;", metadata.imports)
        assertTrue(metadata.enclosingClass!!.contains("class Service"))
        assertTrue(metadata.enclosingMethod!!.contains("names"))
    }

    fun testMetadataOutsideAnyClassOrMethodUsesSafeNulls() {
        val psiFile = myFixture.configureByText(
            "Script.kt",
            """
                val answer = 42
                <caret>
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val metadata = CodeMetadataExtractor.extract(context)

        assertEquals("Kotlin", metadata.language)
        assertEquals("Script.kt", metadata.fileName)
        assertNull(metadata.imports)
        assertNull(metadata.enclosingClass)
        assertNull(metadata.enclosingMethod)
    }

    fun testMetadataForBrokenFileDoesNotCrash() {
        val psiFile = myFixture.configureByText(
            "Broken.kt",
            """
                import kotlin.collections.List

                class Broken {
                    fun bad() {
                        val x = <caret>
                    }
                }
            """.trimIndent()
        )
        val context = myFixture.editorContextFromFixture(psiFile)

        val metadata = CodeMetadataExtractor.extract(context)

        assertEquals("Broken.kt", metadata.fileName)
        assertEquals("Kotlin", metadata.language)
        assertTrue(metadata.imports!!.contains("import kotlin.collections.List"))
        assertTrue(metadata.enclosingClass!!.contains("class Broken"))
        assertTrue(metadata.enclosingMethod!!.contains("fun bad"))
    }
}
