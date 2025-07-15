// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/DeleteTipCommand.kt
package com.x3squaredcircles.photography.application.commands.tip

data class DeleteTipCommand(
    val id: Int
)

data class DeleteTipCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)