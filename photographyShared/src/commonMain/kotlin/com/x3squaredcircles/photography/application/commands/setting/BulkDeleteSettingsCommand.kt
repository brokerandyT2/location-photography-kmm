// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/BulkDeleteSettingsCommand.kt
package com.x3squaredcircles.photography.application.commands.setting

data class BulkDeleteSettingsCommand(
    val keys: List<String>
)

data class BulkDeleteSettingsCommandResult(
    val deletedCount: Int,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)