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