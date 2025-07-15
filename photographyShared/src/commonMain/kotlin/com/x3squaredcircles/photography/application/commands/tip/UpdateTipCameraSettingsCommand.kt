// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/UpdateTipCameraSettingsCommand.kt
package com.x3squaredcircles.photography.application.commands.tip

data class UpdateTipCameraSettingsCommand(
    val id: Int,
    val fstop: String,
    val shutterSpeed: String,
    val iso: String
)

data class UpdateTipCameraSettingsCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)