package jbake

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for the 'jbake.ghpages' plugin, jbake.JbakeGhPagesPlugin.
 */
class JbakeGhPagesPluginTest {

    @Test
    fun `plugin canary test`() {
        assertDoesNotThrow {
            ProjectBuilder
                .builder()
                .build()
                .plugins
                .apply("jbake.ghpages")
        }
    }

    @Test
    fun `plugin type should be JbakeGhPagesPlugin`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("jbake.ghpages")
        project.plugins
            .findPlugin("jbake.ghpages").apply {
                (this is JbakeGhPagesPlugin).apply(::assertTrue)
            }.run { """The plugin "jbake.ghpages" is type of $javaClass""" }
            .run(::println)
    }


    @Test
    fun `check jbakeGreeting task exists`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("jbake.ghpages")
        // Verify the result
        project.tasks.findByName("jbakeGreeting")
            .apply(::assertNotNull)
    }

    @Ignore
    @Test
    fun `check jbakeGreeting task output`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("jbake.ghpages")
        // Verify the result
        project.tasks.findByName("jbakeGreeting")
            .run(::assertNotNull)
    }
}
