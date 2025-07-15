// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/BulkDeleteTipsCommand.kt
package com.x3squaredcircles.photography.application.commands.tip

data class BulkDeleteTipsCommand(
    val tipIds: List<Int>
)

data class BulkDeleteTipsCommandResult(
    val deletedCount: Int,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)