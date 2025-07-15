// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/UpdateTipLocalizationCommand.kt
package com.x3squaredcircles.photography.application.commands.tip

data class UpdateTipLocalizationCommand(
    val id: Int,
    val localization: String
)

data class UpdateTipLocalizationCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)