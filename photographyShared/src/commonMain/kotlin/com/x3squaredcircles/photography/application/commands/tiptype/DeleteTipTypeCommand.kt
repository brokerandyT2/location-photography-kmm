// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tiptype/DeleteTipTypeCommand.kt
package com.x3squaredcircles.photography.application.commands.tiptype

data class DeleteTipTypeCommand(
    val id: Int
)

data class DeleteTipTypeCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)