package jbake

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * JBake plugin.
 */
@Suppress("unused")
class JbakeGhPagesPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register a task
        project.tasks.register("jbakeGreeting") { task ->
            task.doLast {
                """Hello from plugin "jbake.ghpages"""".run(::println)
            }
        }
//        project.buildscript {
//            repositories.mavenCentral()
//            arrayOf(
//                "commons-configuration:commons-configuration:1.10",
//                "org.asciidoctor:asciidoctorj-diagram:3.0.1",
//                "org.asciidoctor:asciidoctorj-diagram-plantuml:1.2025.3",
//            ).map { dependencies.classpath(it) }
//        }
    }
}
