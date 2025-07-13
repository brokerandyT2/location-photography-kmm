// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/commands/UpdateTipTypeCommand.kt
package com.x3squaredcircles.photography.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand
import com.x3squaredcircles.photography.dtos.TipTypeDto

data class UpdateTipTypeCommand(
    val id: Int,
    val name: String = "",
    val description: String = ""
) : ICommand<Result<TipTypeDto>>