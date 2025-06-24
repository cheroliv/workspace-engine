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
    }
}
