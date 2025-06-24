package jbake

import org.gradle.testkit.runner.GradleRunner.create
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class JbakeGhPagesPluginFunctionalTest {

    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }

    private fun initBuildScript(settingsFile: File, buildFile: File) {
        "".run(settingsFile::writeText)
        """plugins { id("jbake.ghpages") }"""
            .run(String::trimIndent)
            .run(buildFile::writeText)
    }

    @Test
    @Suppress("FunctionName")
    fun `can run task`() {
        // Set up the test build
        initBuildScript(settingsFile,buildFile)
        // Verify the output
        """Hello from plugin "jbake.ghpages"""".run(
            create()
                .forwardOutput()
                .withPluginClasspath()
                .withArguments("jbakeGreeting")
                .withProjectDir(projectDir).build().output::contains
        ).run(::assertTrue)
    }


}
