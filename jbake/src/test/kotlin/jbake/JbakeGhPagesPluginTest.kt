package jbake

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.ByteArrayOutputStream
import java.io.PrintStream
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

    @Test
    fun `check jbakeGreeting task output`() {
        // Crée un projet de test et applique le plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("jbake.ghpages")
        val task = project.tasks.findByName("jbakeGreeting")
        assertNotNull(task, "jbakeGreeting task should not be null")

        // Capture la sortie standard
        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))
        try {
            task.actions.forEach { it.execute(task) }
        } finally {
            System.setOut(originalOut)
        }

        // Vérifie la sortie
        val expected = "Hello from plugin \"jbake.ghpages\"\n"
        val actual = outputStream.toString()
        actual.contains(expected)
            .run(::assertTrue)
    }
}
