// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/commands/DeleteCameraBodyCommand.kt
package com.x3squaredcircles.photography.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand

data class DeleteCameraBodyCommand(
    val id: Int
) : ICommand<Result<Boolean>>