// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/DeleteSettingCommand.kt
package com.x3squaredcircles.photography.application.commands.setting

data class DeleteSettingCommand(
    val key: String
)

data class DeleteSettingCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)