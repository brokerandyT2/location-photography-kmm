// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/commands/CreateTipCommand.kt
package com.x3squaredcircles.photography.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand
import com.x3squaredcircles.photography.dtos.TipDto

data class CreateTipCommand(
    val tipTypeId: Int,
    val title: String = "",
    val content: String = "",
    val fstop: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val isUserCreated: Boolean = true
) : ICommand<Result<TipDto>>