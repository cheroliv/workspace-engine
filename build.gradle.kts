import ai.AssistantPlugin
import forms.FormPlugin
import jbake.JBakeGhPagesPlugin
import org.asciidoctor.gradle.jvm.slides.AsciidoctorJRevealJSTask
import school.frontend.SchoolPlugin
import slides.SlidesPlugin
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
import slides.SlidesPlugin.RevealJsSlides.TASK_DASHBOARD_SLIDES_BUILD
import slides.SlidesPlugin.RevealJsSlides.TOC_KEY
import translate.TranslatorPlugin
import workspace.WorkspaceUtils.sep

plugins {
    idea
    this.id("org.asciidoctor.jvm.revealjs")
    this.id("com.github.node-gradle.node")
}

apply<SchoolPlugin>()
apply<FormPlugin>()
apply<JBakeGhPagesPlugin>()
apply<AssistantPlugin>()
apply<TranslatorPlugin>()
apply<SlidesPlugin>()

repositories { ruby { gems() } }

object School {
    const val GROUP_KEY = "artifact.group"
    const val VERSION_KEY = "artifact.version"
    const val SPRING_PROFILE_KEY = "spring.profiles.active"
    const val LOCAL_PROFILE = "local"
}

object Serve {
    const val PACKAGE_NAME = "@serve"
    const val VERSION = "14.2.4"
    const val SERVE_DEP = "$PACKAGE_NAME@$VERSION"
}

object Slide {
    const val OFFICE_FOLDER = "office"
    const val SLIDES_FOLDER = "slides"
    const val WORKSPACE_FOLDER = "workspace"
    const val IMAGES = "images"
    const val DEFAULT_SLIDES_FOLDER = "misc"
    val officeDir: String
        get() = "${System.getProperty("user.home")}$sep$WORKSPACE_FOLDER$sep$OFFICE_FOLDER"
    val DEFAULT_SLIDES_FOLDER_PATH = "$officeDir$sep$SLIDES_FOLDER$sep$DEFAULT_SLIDES_FOLDER"
}

allprojects {
    fun Project.purchaseArtifact() = (School.GROUP_KEY to School.VERSION_KEY).run {
        group = properties[first].toString()
        version = properties[second].toString()
    }
    purchaseArtifact()
}

tasks.run {
    wrapper {
        gradleVersion = "8.14.2"
        distributionType = Wrapper.DistributionType.BIN
    }
    getByName<AsciidoctorJRevealJSTask>(TASK_ASCIIDOCTOR_REVEALJS) {
        group = GROUP_TASK_SLIDER
        description = "Slider settings"
        dependsOn(TASK_CLEAN_SLIDES_BUILD)
        finalizedBy(TASK_DASHBOARD_SLIDES_BUILD)
        revealjs {
            version = "3.1.0"
            templateGitHub {
                setOrganisation("hakimel")
                setRepository("reveal.js")
                setTag("3.9.1")
            }
        }

        revealjsOptions {
            //TODO: passer cette adresse a la configuration du slide pour indiquer sa source,
            // creer une localConf de type slides.SlidesConfiguration
            Slide.DEFAULT_SLIDES_FOLDER_PATH
                .let(::File)
                .apply { println("Slide source absolute path: $absolutePath") }
                .let(::setSourceDir)
            baseDirFollowsSourceFile()
            resources {
                from("${Slide.officeDir}$sep${Slide.IMAGES}") {
                    include("**")
                    into(Slide.IMAGES)
                }
            }
            mapOf(
                BUILD_GRADLE_KEY to layout.projectDirectory
                    .asFile
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

    withType<JavaExec> {
        jvmArgs = listOf(
            "--add-modules=jdk.incubator.vector",
            "--enable-native-access=ALL-UNNAMED",
            "--enable-preview"
        )
    }

    register<Exec>("reportTestApi") {
        group = "api"
        commandLine("./gradlew", "-q", "-s", "-p", "../api", ":reportTests")
    }

    register<Exec>("testApi") {
        group = "api"
        commandLine("./gradlew", "-q", "-s", "-p", "../api", ":check", "--rerun-tasks")
    }

    register<Exec>("runInstaller") {
        group = "installer"
        commandLine(
            "java", "-jar",
            "../api/installer/build/libs/installer-${project.properties["artifact.version"]}.jar"
        )
    }

    register<Exec>("runApi") {
        group = "api"
        commandLine(
            "java", "-jar",
            "../api/build/libs/api-${project.properties["artifact.version"]}.jar"
        )
    }

    register<Exec>("runLocalApi") {
        group = "api"
        commandLine(
            "java", "-D${School.SPRING_PROFILE_KEY}=${School.LOCAL_PROFILE}",
            "-jar", "../api/build/libs/api-${project.properties["artifact.version"]}.jar"
        )
    }

    //TODO: Create another module in api to get cli its own archive(task jar)
    register<Exec>("runCli") {
        group = "api"
        commandLine("./gradlew", "-q", "-s", "-p", "../api", ":cli")
    }

    register("pushTrainingCatalogue") {
        group = "trainings"
        description = "Push training catalogue to remote repository"
        println("push training catalogue to remote repository")
    }

    register("pushSchoolFrontend") {
        group = "trainings"
        description = "Push school frontend to remote repository"
        println("push school frontend to remote repository")
    }

    register("pushSchoolBackoffice") {
        group = "trainings"
        description = "Push school backoffice to remote repository"
        println("push school backoffice  to remote repository")
    }

    //TODO: `serve build/docs/asciidocRevealJs/`
}