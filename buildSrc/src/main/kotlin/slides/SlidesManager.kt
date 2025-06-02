package slides

import git.FileOperationResult
import jbake.SiteConfiguration
import org.gradle.api.Project
import workspace.WorkspaceManager
import workspace.WorkspaceManager.CONFIG_PATH_KEY
import workspace.WorkspaceManager.initAddCommitToSite
import workspace.WorkspaceManager.pushSite
import workspace.WorkspaceManager.readSiteConfigurationFile
import workspace.WorkspaceUtils.sep
import java.io.File
import java.util.*

//TODO: deploy slides to a repo per whole training program https://github.com/talaria-formation/prepro-cda.git

object SlidesManager {

    val Project.slideSrcPath: String get() = "${layout.buildDirectory.get().asFile.absolutePath}/docs/asciidocRevealJs/"
    val Project.slideDestDirPath: String get() = localConf.bake.destDirPath

    val Project.localConf: SiteConfiguration
        get() = readSiteConfigurationFile { "$rootDir$sep${properties[CONFIG_PATH_KEY]}" }


    fun Project.deckFile(key: String): String = buildString {
        append("build/docs/asciidocRevealJs/")
        append(Properties().apply {
            "$projectDir/deck.properties"
                .let(::File)
                .inputStream()
                .use(::load)
        }[key].toString())
    }

    @Suppress("unused")
    fun Project.pushSlides(
        destPath: () -> String,
        pathTo: () -> String
    ) = pathTo()
        .run(WorkspaceManager::createRepoDir)
        .let { it: File ->
            copySlideFilesToRepo(destPath(), it)
                .takeIf { it is FileOperationResult.Success }
                ?.run {
                    initAddCommitToSite(it, localConf)
                    pushSite(it, localConf)
                    it.deleteRecursively()
                    destPath()
                        .let(::File)
                        .deleteRecursively()
                }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    fun copySlideFilesToRepo(
        slidesDirPath: String,
        repoDir: File
    ): FileOperationResult = try {
        slidesDirPath
            .let(::File)
            .apply {
                when {
                    !copyRecursively(
                        repoDir,
                        true
                    ) -> throw Exception("Unable to copy slides directory to build directory")
                }
            }.deleteRecursively()
        FileOperationResult.Success
    } catch (e: Exception) {
        FileOperationResult.Failure(e.message ?: "An error occurred during file copy.")
    }
}


/*package slides

import com.fasterxml.jackson.module.kotlin.readValue
import git.FileOperationResult
import git.GitPushConfiguration
import git.RepositoryConfiguration
import git.RepositoryCredentials
import org.gradle.api.Project
import workspace.WorkspaceManager
import workspace.WorkspaceManager.CONFIG_PATH_KEY
import workspace.WorkspaceManager.initAddCommitToSite
import workspace.WorkspaceManager.pushSite
import workspace.WorkspaceUtils.sep
import workspace.WorkspaceUtils.yamlMapper
import java.io.File
import java.util.*

//TODO: deploy slides to a repo per whole training program https://github.com/talaria-formation/prepro-cda.git

object SlidesManager {

    val Project.slideSrcPath: String get() = "${layout.buildDirectory.get().asFile.absolutePath}/docs/asciidocRevealJs/"

    val Project.slideDestDirPath: String get() = localConf.bake.destDirPath

    fun Project.readSlideConfigurationFile(
        configPath: () -> String
    ): SlidesConfiguration = try {
        configPath()
            .run(::File)
            .run(yamlMapper::readValue)
    } catch (e: Exception) {
        // Handle exception or log error
        SlidesConfiguration(
            srcPath = "",
            pushPage = GitPushConfiguration(
                "",
                "",
                RepositoryConfiguration(
                    "",
                    "",
                    RepositoryCredentials("", "")
                ),
                "",
                ""
            )
        )
    }

    val Project.localConf: SlidesConfiguration
        get() = readSlideConfigurationFile { "$rootDir$sep${properties[CONFIG_PATH_KEY]}" }


    fun Project.deckFile(key: String): String = buildString {
        append("build/docs/asciidocRevealJs/")
        append(Properties().apply {
            "$projectDir/deck.properties"
                .let(::File)
                .inputStream()
                .use(::load)
        }[key].toString())
    }

    @Suppress("unused")
    fun Project.pushSlides(
        destPath: () -> String,
        pathTo: () -> String
    ) = pathTo()
        .run(WorkspaceManager::createRepoDir)
        .let { it: File ->
            copySlideFilesToRepo(destPath(), it)
                .takeIf { it is FileOperationResult.Success }
                ?.run {
                    initAddCommitToSite(it, localConf)
                    pushSite(it, localConf)
                    it.deleteRecursively()
                    destPath()
                        .let(::File)
                        .deleteRecursively()
                }
        }

    @Suppress("MemberVisibilityCanBePrivate")
    fun copySlideFilesToRepo(
        slidesDirPath: String,
        repoDir: File
    ): FileOperationResult = try {
        slidesDirPath
            .let(::File)
            .apply {
                when {
                    !copyRecursively(
                        repoDir,
                        true
                    ) -> throw Exception("Unable to copy slides directory to build directory")
                }
            }.deleteRecursively()
        FileOperationResult.Success
    } catch (e: Exception) {
        FileOperationResult.Failure(e.message ?: "An error occurred during file copy.")
    }
}*/