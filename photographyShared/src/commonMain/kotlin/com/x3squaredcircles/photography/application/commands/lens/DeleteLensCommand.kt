// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/lens/DeleteLensCommand.kt
package com.x3squaredcircles.photography.application.commands.lens

data class DeleteLensCommand(
    val id: Int
)

data class DeleteLensCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)