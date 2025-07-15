// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tiptype/UpdateTipTypeLocalizationCommand.kt
package com.x3squaredcircles.photography.application.commands.tiptype

data class UpdateTipTypeLocalizationCommand(
    val id: Int,
    val localization: String
)

data class UpdateTipTypeLocalizationCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)