import java.io.File.separator

plugins {
    `java-gradle-plugin`
    this.alias(libs.plugins.kotlin.jvm)
}

repositories(RepositoryHandler::mavenCentral)

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

gradlePlugin {
    val jbakeGhPages by plugins.creating {
        id = "jbake.ghpages"
        implementationClass = "jbake.JbakeGhPagesPlugin"
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["functionalTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])

// Add a task to run the functional tests
val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform()
}

gradlePlugin.testSourceSets.add(functionalTestSourceSet)

tasks.named<Task>("check") { dependsOn(functionalTest) }

tasks.named<Test>("test") { useJUnitPlatform() }

//kotlin.jvmToolchain {
//    org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21.ordinal
//        .run(JavaLanguageVersion::of)
//        .run(languageVersion::set)
//}
//
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21.run(compilerOptions.jvmTarget::set)
//}

/*
import org.apache.commons.lang.SystemUtils.USER_HOME
import java.io.ByteArrayOutputStream

buildscript {
    repositories.mavenCentral()
    arrayOf(
        "commons-configuration:commons-configuration:1.10",
        "org.asciidoctor:asciidoctorj-diagram:3.0.1",
        "org.asciidoctor:asciidoctorj-diagram-plantuml:1.2025.3",
    ).map { dependencies.classpath(it) }
}

plugins { this.id("org.jbake.site").version("5.5.0") }

val jbakeRuntime: Configuration by configurations.creating {
    description = "Classpath for running Jbake core directly"
}

dependencies { jbakeRuntime("org.jbake:jbake-core:2.7.0-rc.7") }

jbake {
    srcDirName = "src/jbake".run(project::file).path
    destDirName = "build/jbake".run(project.layout.buildDirectory.get()::file).asFile.path
    configuration["asciidoctor.option.requires"] = "asciidoctor-diagram"
//    configuration["asciidoctor.attributes"] = arrayOf(
//        "sourceDir=${projectDir}",
//        "imagesDir=diagrams",
//        "imagesoutdir=${tasks.bake.get().input}/assets/diagrams")
}

tasks.register<JavaExec>("serve") {
    group = "managed-jbake"
    mainClass.set("org.jbake.launcher.Main")
//    dependsOn(tasks.bake)
    classpath = jbakeRuntime
    args = listOf(
        "-b",
        "src/jbake"
            .run(project::file)
            .absolutePath,
        "-s",
        "jbake"
            .run(project.layout.buildDirectory.get().asFile::resolve)
            .absolutePath,
    )
    doFirst {
        "Serving $group at: https://localhost:8820/"
            .run(::println)
    }
}

//TDOD: Add in task initialize the generation of the site yml configuration file.
// si il trouve pas de fichier de configuration, il faut le créer
// sinon laisser le fichier de configuration existant
// et chercher si il trouve un fichier de configuration jbake.properties dans la valeur srcDirPath du fichier de configuration
// ajouter l'installation de jbake si il n'est pas installé dans l'action(github action) initialize
tasks.register<Exec>("initialize") {
    group = project.projectDir.name
    val srcDirPath = "src/jbake".apply {
        run(project::file)
            .run { if (!exists()) mkdirs() }
    }
    val baker = "$USER_HOME/.sdkman/candidates/jbake/2.6.7/bin/jbake".apply {
        if (!run(::File).exists()) {
            "Jbake executable not found at: $this".run(::println)
            throw "Jbake executable not found".run(::IllegalStateException)
        }
    }
    doFirst {
        srcDirPath
            .run { "Initializing Jbake source directory at: $this" }
            .run(::println)
    }
    commandLine = listOf(baker, "-i", srcDirPath)
    workingDir = projectDir
    standardOutput = ByteArrayOutputStream()
}
*/