// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tiptype/BulkUpdateTipTypesCommand.kt
package com.x3squaredcircles.photography.application.commands.tiptype

data class BulkUpdateTipTypesCommand(
    val tipTypes: List<TipTypeUpdateData>
)

data class TipTypeUpdateData(
    val id: Int,
    val name: String,
    val i8n: String = "en-US"
)

data class BulkUpdateTipTypesCommandResult(
    val updatedCount: Int,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)