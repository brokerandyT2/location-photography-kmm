// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/UpdateSettingCommand.kt
package com.x3squaredcircles.photography.application.commands.setting

import com.x3squaredcircles.core.domain.entities.Setting

data class UpdateSettingCommand(
    val key: String,
    val value: String,
    val description: String = ""
)

data class UpdateSettingCommandResult(
    val setting: Setting?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)