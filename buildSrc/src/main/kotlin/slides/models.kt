package slides

import git.GitPushConfiguration

@JvmRecord
data class SlidesConfiguration(
    val srcPath: String,
    val pushPage: GitPushConfiguration,
)