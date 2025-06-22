@file:Suppress("FunctionName")

package jbake

import org.gradle.testkit.runner.GradleRunner.create
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class JbakeGhpagesPluginFunctionalTest {

    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }

    @Test
    fun `can run task`() {
        // Set up the test build
        "".run(settingsFile::writeText)
        """plugins { id("jbake.greeting") }"""
            .run(String::trimIndent)
            .run(buildFile::writeText)

        val runner = create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("greeting")
            .withProjectDir(projectDir)
        val result = runner.build().output
        // Verify the output
        """Hello from plugin "jbake.greeting""""
            .run(result::contains)
            .run(::assertTrue)
    }
}
