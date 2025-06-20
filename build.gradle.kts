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
import slides.SlidesPlugin.RevealJsSlides.TASK_DASHBOARD_SLIDES_BUILD
import slides.SlidesPlugin.RevealJsSlides.TOC_KEY
import slides.SlidesPlugin.Slide.DEFAULT_SLIDES_FOLDER_PATH
import slides.SlidesPlugin.Slide.IMAGES
import workspace.WorkspaceUtils.sep

plugins { this.id("org.asciidoctor.jvm.revealjs") }

apply<school.frontend.SchoolPlugin>()
apply<forms.FormPlugin>()
apply<jbake.JBakeGhPagesPlugin>()
apply<ai.AssistantPlugin>()
apply<translate.TranslatorPlugin>()
apply<slides.SlidesPlugin>()
apply<api.ApiPlugin>()
apply<workspace.WorkspacePlugin>()
apply<school.training.content.TrainingContentPlugin>()
apply<school.training.courses.CoursesPlugin>()

project.tasks.getByName<AsciidoctorJRevealJSTask>(TASK_ASCIIDOCTOR_REVEALJS) {
    repositories { ruby { gems() } }
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
        DEFAULT_SLIDES_FOLDER_PATH
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
