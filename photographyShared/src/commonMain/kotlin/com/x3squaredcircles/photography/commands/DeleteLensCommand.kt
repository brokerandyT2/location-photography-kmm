// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/commands/DeleteLensCommand.kt
package com.x3squaredcircles.photography.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand

data class DeleteLensCommand(
    val id: Int
) : ICommand<Result<Boolean>>