package slides

import git.GitPushConfiguration

@JvmRecord
data class SlidesConfiguration(
    val srcPath: String?=null,
    val pushSlides: GitPushConfiguration?=null,
)