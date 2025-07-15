// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/DeleteTipsByTypeCommand.kt
package com.x3squaredcircles.photography.application.commands.tip

data class DeleteTipsByTypeCommand(
    val tipTypeId: Int
)

data class DeleteTipsByTypeCommandResult(
    val deletedCount: Int,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)