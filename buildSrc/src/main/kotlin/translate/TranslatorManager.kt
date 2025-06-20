@file:Suppress("MemberVisibilityCanBePrivate")

package translate

import ai.AssistantManager.createOllamaChatModel
import ai.AssistantManager.createOllamaStreamingChatModel
import ai.AssistantManager.generateStreamingResponse
import ai.AssistantManager.localModels
import arrow.core.Either.Left
import arrow.core.Either.Right
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.options.Option
import org.gradle.kotlin.dsl.register
import translate.TranslatorManager.PromptManager.getTranslatePromptMessage
import workspace.WorkspaceUtils.uppercaseFirstChar
import java.util.Locale.*

object TranslatorManager {
    val MODEL: String = localModels.find { it.second == "LlamaTiny" }
        ?.first ?: ""

    @JvmStatic
    fun main(args: Array<String>) {
        @Suppress("SimplifyNestedEachInScopeFunction")
        supportedLanguages
            .translationTasks()
            .apply { forEach(::println) }
            .let {
                PromptManager.userLanguage.run { "userLanguage : $this" }.run(::println)
                "Gradle task name : ${it.first().first}".run(::println)
                it.first().second.getTranslatePromptMessage("SALUT LES AMIS COMMENT CA VA ?")
                    .run { "translateMessage : $this" }.run(::println)
            }
    }

    @JvmStatic
    val supportedLanguages: Set<String> = setOf(
        FRENCH, ENGLISH/*, GERMAN,
        ITALIAN, SIMPLIFIED_CHINESE,
        forLanguageTag("ru"),
        forLanguageTag("es"),
        forLanguageTag("pt"),*/
    ).map { it.language }.toSet()

    @JvmStatic
    fun Set<String>.translationTasks()
            : Set<Pair<String/* taskName */, Pair<String/* from */, String/* to */>>> =
        mutableSetOf<Pair<String, Pair<String, String>>>().apply {
            this@translationTasks
                .map { it.uppercaseFirstChar }
                .run langNames@{
                    forEach { from: String ->
                        "${from}To".run task@{
                            this@langNames
                                .filter { it != from }
                                .forEach { add("${this@task}$it" to (from to it)) }
                        }
                    }
                }
        }

    object PromptManager {
        @JvmStatic
        val userLanguage = System.getProperty("user.language")!!

        @JvmStatic
        val fromLanguage = System.getProperty("user.language")!!

        @JvmStatic
        fun Pair<String, String>.getTranslatePromptMessage(text: String): String =
            """Translate this sentence from ${
                forLanguageTag(first).getDisplayLanguage(ENGLISH).lowercase()
            } to ${
                forLanguageTag(second).getDisplayLanguage(ENGLISH).lowercase()
            } :
$text""".trimMargin()
    }


    fun Project.createDisplaySupportedLanguagesTask() =
        tasks.register<DefaultTask>("displaySupportedLanguages") {
            group = "translator"
            description = "Dislpay supported languages"
            doFirst {
                supportedLanguages.map { "${forLanguageTag(it).displayLanguage}($it) " }
                    .run { "supportedLanguages : $this" }
                    .run(::println)
            }
        }


    // Creating tasks for each model
    fun Project.createTranslationTasks(): Unit = run {
        supportedLanguages
            .translationTasks()
            .forEach {
                createTranslationChatTask(MODEL, it)
                createStreamingTranslationChatTask(MODEL, it)
            }
    }

    open class InputTranslationTextTask : DefaultTask() {
        @set:Option(option = "text", description = "The text to translate")
        @get:Input
        lateinit var text: String
    }

    // Generic function for chat model tasks
    fun Project.createTranslationChatTask(
        model: String,
        taskComponent: Pair<String, Pair<String, String>>
    ) {
        tasks.register<InputTranslationTextTask>("translate${taskComponent.first}") {
            group = "translator"
            description = "Translate using the Ollama $model chatgpt prompt request."
            doLast {
                project.runTranslationChat(
                    model,
                    taskComponent.second.getTranslatePromptMessage(text)
                )
            }
        }
    }

    // Generic function for streaming chat model tasks
    fun Project.createStreamingTranslationChatTask(
        model: String,
        taskComponent: Pair<String, Pair<String, String>>
    ) {
        tasks.register<InputTranslationTextTask>("translateStream${taskComponent.first}") {
            group = "translator"
            description = "Translate the Ollama $model chatgpt stream prompt request."
            doLast {
                project.runStreamTranslationChat(
                    model,
                    taskComponent.second.getTranslatePromptMessage(text)
                )
            }
        }
    }

    fun Project.runTranslationChat(model: String, text: String) =
        createOllamaChatModel(model = model).run { chat(text).let(::println) }

    fun Project.runStreamTranslationChat(model: String, text: String) = runBlocking {
        createOllamaStreamingChatModel(model).run {
            when (val answer = generateStreamingResponse(this, text)) {
                is Right -> "Complete response received:\n${
                    answer.value.content().text()
                }".run(::println)

                is Left -> "Error during response generation:\n${answer.value}".run(::println)
            }
        }
    }
}