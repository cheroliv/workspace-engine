import jbake.JBakeGhPagesManager.createCnameFile
import jbake.JBakeGhPagesManager.sitePushDestPath
import jbake.JBakeGhPagesManager.sitePushPathTo
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
import workspace.WorkspaceManager.GROUP_TASK_SITE
import workspace.WorkspaceManager.TASK_BAKE_SITE
import workspace.WorkspaceManager.TASK_PUBLISH_SITE
import workspace.WorkspaceManager.bakeDestDirPath
import workspace.WorkspaceManager.bakeSrcPath
import workspace.WorkspaceManager.pushSiteToGhPages
import workspace.WorkspaceUtils.sep

plugins {
    id("org.jbake.site")
    id("org.asciidoctor.jvm.revealjs")
}

apply<slides.SlidesPlugin>()
apply<school.courses.CoursesPlugin>()

repositories { ruby { gems() } }


tasks.getByName<AsciidoctorJRevealJSTask>(TASK_ASCIIDOCTOR_REVEALJS) {
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

tasks.register<DefaultTask>(TASK_PUBLISH_SITE) {
    group = GROUP_TASK_SITE
    description = "Publish site online."
    dependsOn(TASK_BAKE_SITE)
    doFirst { createCnameFile() }
    jbake {
        srcDirName = bakeSrcPath
        destDirName = bakeDestDirPath
    }
    doLast { pushSiteToGhPages(sitePushDestPath(), sitePushPathTo()) }
}