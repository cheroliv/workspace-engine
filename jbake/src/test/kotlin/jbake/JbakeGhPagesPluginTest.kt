package jbake

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Unit tests for the 'jbake.ghpages' plugin, jbake.JbakeGhPagesPlugin.
 */
class JbakeGhPagesPluginTest {
    @Test
    fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("jbake.ghpages")

        // Verify the result
        assertNotNull(project.tasks.findByName("jbakeGreeting"))
    }
}
