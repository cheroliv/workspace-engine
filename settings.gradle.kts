rootProject.name = "workspace-engine"
include("workspace", "jbake-ghpages")

pluginManagement {
    repositories.gradlePluginPortal()
    plugins {
        kotlin("jvm").version("2.1.21")
        id("org.jbake.site").version(extra["jbake-gradle.version"].toString())
        id("com.github.node-gradle.node").version(extra["node-gradle.version"].toString())
        id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement.repositories.mavenCentral()
