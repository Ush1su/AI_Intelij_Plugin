package com.example.ai_explainer_plugin.services.llm

import com.example.ai_explainer_plugin.services.llm.prompts.AUTOCOMPLETE_PROMPT
import com.example.ai_explainer_plugin.services.llm.prompts.EXPLAIN_PROMPT
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

@Service(Service.Level.PROJECT)
class LLMService(
    @Suppress("unused") private val project: Project,
    @Suppress("unused") private val scope: CoroutineScope,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val apiKey = System.getenv("OPENAI_API_KEY")
        ?.takeIf { it.isNotBlank() }
        ?: error("OPENAI_API_KEY is not set")

    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    suspend fun explain(content: String): String {
        val userPrompt = """
            Explain the following content:

            ```
            $content
            ```
        """.trimIndent()

        return ask(EXPLAIN_PROMPT, userPrompt)
    }

    suspend fun autocomplete(
        beforeCursor: String,
        afterCursor: String,
        userWrittenPrompt: String,
    ): String {
        val userPrompt = """
            Context before cursor:
            ```
            $beforeCursor
            ```

            Context after cursor:
            ```
            $afterCursor
            ```
            User prompt:
            $userWrittenPrompt
            
            Return only the completion to insert at the cursor.
        """.trimIndent()

        return ask(AUTOCOMPLETE_PROMPT, userPrompt)
    }

    private suspend fun ask(
        systemPrompt: String,
        userPrompt: String,
    ): String = withContext(Dispatchers.IO) {

        val requestBody = ResponsesRequest(
            model = MODEL,
            input = listOf(
                Message(role = "developer", content = systemPrompt),
                Message(role = "user", content = userPrompt),
            )
        )

        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/responses"))
            .timeout(Duration.ofSeconds(60))
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json.encodeToString(requestBody)))
            .build()

        val response = httpClient
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .await()

        if (response.statusCode() !in 200..299) {
            error("OpenAI request failed: ${response.statusCode()} ${response.body()}")
        }

        val parsed = json.decodeFromString<ResponsesResponse>(response.body())

        parsed.output
            .flatMap { it.content }
            .firstOrNull { it.type == "output_text" }
            ?.text
            ?.trim()
            .orEmpty()
    }

    companion object {
        private const val MODEL = "gpt-5-nano"
    }
}

@Serializable
private data class ResponsesRequest(
    val model: String,
    val input: List<Message>,
    @SerialName("max_output_tokens")
    val maxOutputTokens: Int = 500
)

@Serializable
private data class Message(
    val role: String,
    val content: String,
)

@Serializable
private data class ResponsesResponse(
    val output: List<OutputItem> = emptyList(),
)

@Serializable
private data class OutputItem(
    val content: List<OutputContent> = emptyList(),
)

@Serializable
private data class OutputContent(
    val type: String? = null,
    val text: String? = null,
)