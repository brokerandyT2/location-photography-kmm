// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tiptype/BulkDeleteTipTypesCommand.kt
package com.x3squaredcircles.photography.application.commands.tiptype

data class BulkDeleteTipTypesCommand(
    val tipTypeIds: List<Int>
)

data class BulkDeleteTipTypesCommandResult(
    val deletedCount: Int,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)