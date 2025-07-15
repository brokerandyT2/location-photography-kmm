// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/BulkUpsertSettingsCommand.kt
package com.x3squaredcircles.photography.application.commands.setting

data class BulkUpsertSettingsCommand(
    val keyValuePairs: Map<String, String>
)

data class BulkUpsertSettingsCommandResult(
    val upsertedSettings: Map<String, String>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)