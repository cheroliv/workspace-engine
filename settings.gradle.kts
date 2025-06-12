rootProject.name = "workspace-engine"

pluginManagement {
    repositories.gradlePluginPortal()
    plugins {
        id("org.jbake.site").version(extra["jbake-gradle.version"].toString())
        id("com.github.node-gradle.node").version(extra["node-gradle.version"].toString())
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement.repositories.mavenCentral()
