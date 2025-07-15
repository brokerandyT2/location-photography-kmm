// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/CreateSettingCommand.kt
package com.x3squaredcircles.photography.application.commands.setting

import com.x3squaredcircles.core.domain.entities.Setting

data class CreateSettingCommand(
    val key: String,
    val value: String,
    val description: String = ""
)

data class CreateSettingCommandResult(
    val setting: Setting,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)