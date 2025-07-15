// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/BulkUpdateTipsCommand.kt
package com.x3squaredcircles.photography.application.commands.tip

data class BulkUpdateTipsCommand(
    val tips: List<TipUpdateData>
)

data class TipUpdateData(
    val id: Int,
    val tipTypeId: Int,
    val title: String,
    val content: String,
    val fstop: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val i8n: String = "en-US"
)

data class BulkUpdateTipsCommandResult(
    val updatedCount: Int,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)