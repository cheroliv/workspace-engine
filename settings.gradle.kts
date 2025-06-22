rootProject.name = "workspace-engine"
include("workspace", "jbake-ghpages")

pluginManagement {
    repositories.gradlePluginPortal()
    plugins {
        kotlin("jvm").version("2.1.21")
        id("org.jbake.site").version(extra["jbake-gradle.version"].toString())
        id("com.github.node-gradle.node").version(extra["node-gradle.version"].toString())
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement.repositories.mavenCentral()
