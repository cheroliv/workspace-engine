package slides

import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.repositories
import slides.SlidesManager.deckFile
import slides.SlidesPlugin.RevealJsSlides.TASK_DASHBOARD_SLIDES_BUILD
import workspace.WorkspaceManager.localConf
import workspace.WorkspaceUtils.yamlMapper
import java.io.File


/**
 * repos needed:
 *  - slideshowroom
 */
class SlidesPlugin : Plugin<Project> {
    object RevealJsSlides {
        const val GROUP_TASK_SLIDER = "slider"
        const val TASK_ASCIIDOCTOR_REVEALJS = "asciidoctorRevealJs"
        const val TASK_CLEAN_SLIDES_BUILD = "cleanSlidesBuild"
        const val TASK_DASHBOARD_SLIDES_BUILD = "dashSlidesBuild"
        const val TASK_PUBLISH_SLIDE = "publishSlide"
        const val BUILD_GRADLE_KEY = "build-gradle"
        const val ENDPOINT_URL_KEY = "endpoint-url"
        const val SOURCE_HIGHLIGHTER_KEY = "source-highlighter"
        const val CODERAY_CSS_KEY = "coderay-css"
        const val IMAGEDIR_KEY = "imagesdir"
        const val TOC_KEY = "toc"
        const val ICONS_KEY = "icons"
        const val SETANCHORS_KEY = "setanchors"
        const val IDPREFIX_KEY = "idprefix"
        const val IDSEPARATOR_KEY = "idseparator"
        const val DOCINFO_KEY = "docinfo"
        const val REVEALJS_THEME_KEY = "revealjs_theme"
        const val REVEALJS_TRANSITION_KEY = "revealjs_transition"
        const val REVEALJS_HISTORY_KEY = "revealjs_history"
        const val REVEALJS_SLIDENUMBER_KEY = "revealjs_slideNumber"
    }

    override fun apply(project: Project) {
        project.repositories {
            mavenCentral()
            gradlePluginPortal()
        }

        project.tasks.register<DefaultTask>("cleanSlidesBuild") {
            group = "slider"
            description = "Delete generated presentation in build directory."
            doFirst {
                "${project.layout.buildDirectory}/docs/asciidocRevealJs".run {
                    "$this/images"
                        .let(::File)
                        .deleteRecursively()
                    let(::File)
                        .listFiles()
                        ?.filter { it.isFile && it.name.endsWith(".html") }
                        ?.forEach { it.delete() }
                }
            }
        }

        project.tasks.register<Exec>("openFirefox") {
            group = "slider"
            description = "Open the default.deck.file presentation in firefox"
            dependsOn("asciidoctor")
            commandLine("firefox", project.deckFile("default.deck.file"))
            workingDir = project.layout.projectDirectory.asFile
        }

        project.tasks.register<Exec>("openChromium") {
            group = "slider"
            description = "Open the default.deck.file presentation in chromium"
            dependsOn("asciidoctor")
            commandLine("chromium", project.deckFile("default.deck.file"))
            workingDir = project.layout.projectDirectory.asFile
        }

        project.tasks.register(TASK_DASHBOARD_SLIDES_BUILD) {
            group = "documentation"
            description = "Génère un index.html listant toutes les présentations Reveal.js"

            doLast {
                //TODO: passer cette adresse a la configuration du slide pour indiquer sa source
                val slidesDir = listOf(
                    System.getProperty("user.home"),
                    "workspace", "office", "slides", "misc"
                ).reduce { acc, part -> File(acc, part).path }
                    .let(::File)
                    .apply {
                        listFiles().find {
                            it.name == "index.html"
                        }!!.readText().trimIndent()
                            .run { "index.html:\n$this" }
//                            .apply(::println)
                            .run(project.logger::info)
                    }

                project.layout.buildDirectory
                    .get().asFile.path
//                            .apply(::println)
                    .run(project.logger::info)

                val outputDir = project.layout.buildDirectory.get().asFile
                    .run { "$this/docs/asciidocRevealJs" }
                    .run(::File)
                    .apply {
                        "output dir path: $this"
//                            .apply(::println)
                            .run(project.logger::info)
                    }

                val indexFile: File = "$slidesDir/index.html"
                    .run(::File)
                    .apply {
                        readText().trimIndent()
                            .run { "index.html:\n$this" }
                            .apply(::println)
//                            .run(project.logger::info)
                    }
                val slidesJsonFile = File("$outputDir/slides.json")

//
//                // Créer le dossier de sortie s'il n'existe pas
//                outputDir.mkdirs()
//
//                // Scanner les fichiers .adoc dans le dossier slides
//                val adocFiles = slidesDir.listFiles { file ->
//                    file.isFile && file.extension == "adoc"
//                }?.map { file ->
//                    mapOf(
//                        "name" to file.nameWithoutExtension,
//                        "filename" to "${file.nameWithoutExtension}.html"
//                    )
//                } ?: emptyList()
//
//                // Générer le fichier slides.json
//                val jsonContent = buildString {
//                    appendLine("[")
//                    adocFiles.forEachIndexed { index, slide ->
//                        append("  {")
//                        append("\"name\": \"${slide["name"]}\", ")
//                        append("\"filename\": \"${slide["filename"]}\"")
//                        append("}")
//                        if (index < adocFiles.size - 1) append(",")
//                        appendLine()
//                    }
//                    appendLine("]")
//                }
//
//                slidesJsonFile.writeText(jsonContent)
//
//
//                // Générer le fichier index.html
//                val htmlContent = slidesDir.listFiles()
//                    .find { it.name == "index.html" }!!
//                    .readText().trimIndent()
//
//                indexFile.writeText(htmlContent)
//
//                println("✅ Dashboard généré avec succès !")
//                println("📁 Fichiers générés :")
//                println("   - ${indexFile.absolutePath}")
//                println("   - ${slidesJsonFile.absolutePath}")
//                println("📊 ${adocFiles.size} présentation(s) trouvée(s)")
            }
        }

        //TODO: passer cette tache de script en tache programmatique de plugin
        /*
    import org.asciidoctor.gradle.jvm.slides.AsciidoctorJRevealJSTask
    import slides.SlidesPlugin.RevealJsSlides.BUILD_GRADLE_KEY
    import slides.SlidesPlugin.RevealJsSlides.CODERAY_CSS_KEY
    import slides.SlidesPlugin.RevealJsSlides.DOCINFO_KEY
    import slides.SlidesPlugin.RevealJsSlides.ENDPOINT_URL_KEY
    import slides.SlidesPlugin.RevealJsSlides.GROUP_TASK_SLIDER
    import slides.SlidesPlugin.RevealJsSlides.ICONS_KEY
    import slides.SlidesPlugin.RevealJsSlides.IDPREFIX_KEY
    import slides.SlidesPlugin.RevealJsSlides.IDSEPARATOR_KEY
    import slides.SlidesPlugin.RevealJsSlides.IMAGEDIR_KEY
    import slides.SlidesPlugin.RevealJsSlides.REVEALJS_HISTORY_KEY
    import slides.SlidesPlugin.RevealJsSlides.REVEALJS_SLIDENUMBER_KEY
    import slides.SlidesPlugin.RevealJsSlides.REVEALJS_THEME_KEY
    import slides.SlidesPlugin.RevealJsSlides.REVEALJS_TRANSITION_KEY
    import slides.SlidesPlugin.RevealJsSlides.SETANCHORS_KEY
    import slides.SlidesPlugin.RevealJsSlides.SOURCE_HIGHLIGHTER_KEY
    import slides.SlidesPlugin.RevealJsSlides.TASK_ASCIIDOCTOR_REVEALJS
    import slides.SlidesPlugin.RevealJsSlides.TASK_CLEAN_SLIDES_BUILD
    import slides.SlidesPlugin.RevealJsSlides.TOC_KEY
    import workspace.WorkspaceUtils.sep

    plugins {
        id("org.asciidoctor.jvm.revealjs")
    }

    tasks.getByName<AsciidoctorJRevealJSTask>(TASK_ASCIIDOCTOR_REVEALJS) {
        group = GROUP_TASK_SLIDER
        description = "Slider settings"
        dependsOn(TASK_CLEAN_SLIDES_BUILD)
        revealjs {
            version = "3.1.0"
            templateGitHub {
                setOrganisation("hakimel")
                setRepository("reveal.js")
                setTag("3.9.1")
            }
        }
        val OFFICE = "office"
        val SLIDES = "slides"
        val IMAGES = "images"
        revealjsOptions {
            //TODO: passer cette adresse a la configuration du slide pour indiquer sa source
            "${System.getProperty("user.home")}${sep}workspace$sep$OFFICE$sep$SLIDES${sep}misc"
                .let(::File)
                .apply { println("Slide source absolute path: $absolutePath") }
                .let(::setSourceDir)
            baseDirFollowsSourceFile()
            resources {
                from("$sourceDir$sep$IMAGES") {
                    include("**")
                    into(IMAGES)
                }
            }
            mapOf(
                BUILD_GRADLE_KEY to layout.projectDirectory
                    .let { "$it${sep}build.gradle.kts" }
                    .let(::File),
                ENDPOINT_URL_KEY to "https://github.com/pages-content/slides/",
                SOURCE_HIGHLIGHTER_KEY to "coderay",
                CODERAY_CSS_KEY to "style",
                IMAGEDIR_KEY to ".${sep}images",
                TOC_KEY to "left",
                ICONS_KEY to "font",
                SETANCHORS_KEY to "",
                IDPREFIX_KEY to "slide-",
                IDSEPARATOR_KEY to "-",
                DOCINFO_KEY to "shared",
                REVEALJS_THEME_KEY to "black",
                REVEALJS_TRANSITION_KEY to "linear",
                REVEALJS_HISTORY_KEY to "true",
                REVEALJS_SLIDENUMBER_KEY to "true"
            ).let(::attributes)
        }
    }
   */




        project.tasks.register<DefaultTask>("deploySlides") {
            group = "slider"
            description = "Deploy sliders to remote repository"
            dependsOn("asciidoctor")
            doFirst { "Task description :\n\t$description".run(project.logger::info) }
            doLast {
                project.localConf
                    .let(project.yamlMapper::writeValueAsString)
                    .let(project.logger::info)
//                project.workspaceEither.fold(
//                    { "Error: $it".run(::println) },
//                    { it: Office ->
//
//                        it.also(::println)
//                            .let(project.yamlMapper::writeValueAsString)
//                            .let(::println)
//                    }
//                )
//                println("path :\n\t${project.layout.buildDirectory.get().asFile.absolutePath}/docs/asciidocRevealJs/")
//                project.slideSrcPath
//                    .let(::File)
//                    .listFiles()!!
////            .forEach { it.name.let(::println) }
////            pushSlides(destPath = { slideDestDirPath },
////                pathTo = { "${layout.buildDirectory.get().asFile.absolutePath}${getDefault().separator}${localConf.pushPage.to}" })
//                println("Affiche la config slide")
//                project.printConf()
            }
        }

        project.tasks.register<AsciidoctorTask>("asciidoctor") {
            group = "slider"
            dependsOn(project.tasks.findByPath("asciidoctorRevealJs"))
        }

        project.tasks.register<Exec>("asciidocCapsule") {
            group = "capsule"
            dependsOn("asciidoctor")
            commandLine("chromium", project.deckFile("asciidoc.capsule.deck.file"))
            workingDir = project.layout.projectDirectory.asFile
        }
    }
}
