// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/commands/CreateTipTypeCommand.kt
package com.x3squaredcircles.photography.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand
import com.x3squaredcircles.photography.dtos.TipTypeDto

data class CreateTipTypeCommand(
    val name: String = "",
    val description: String = "",
    val isUserCreated: Boolean = true
) : ICommand<Result<TipTypeDto>>