// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/camerabody/DeleteCameraBodyCommand.kt
package com.x3squaredcircles.photography.application.commands.camerabody

data class DeleteCameraBodyCommand(
    val id: Int
)

data class DeleteCameraBodyCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)