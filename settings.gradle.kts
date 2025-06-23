rootProject.name = "workspace-engine"
include("workspace", "jbake")

pluginManagement {
    repositories.gradlePluginPortal()
    plugins {
        kotlin("jvm").version(extra["kotlin.version"].toString())
        id("org.jbake.site").version(extra["jbake-gradle.version"].toString())
        id("com.github.node-gradle.node").version(extra["node-gradle.version"].toString())
//        id("org.gradle.toolchains.foojay-resolver-convention").version(extra["toolchains.version"].toString())
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement.repositories.mavenCentral()
