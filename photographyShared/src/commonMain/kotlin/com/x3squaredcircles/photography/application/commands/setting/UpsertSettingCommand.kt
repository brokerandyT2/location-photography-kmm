// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/UpsertSettingCommand.kt
package com.x3squaredcircles.photography.application.commands.setting

import com.x3squaredcircles.core.domain.entities.Setting

data class UpsertSettingCommand(
    val key: String,
    val value: String,
    val description: String = ""
)

data class UpsertSettingCommandResult(
    val setting: Setting,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)