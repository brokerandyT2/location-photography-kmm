// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/commands/UpdateLensCommand.kt
package com.x3squaredcircles.photography.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand
import com.x3squaredcircles.photography.dtos.LensDto

data class UpdateLensCommand(
    val id: Int,
    val minMM: Double = 0.0,
    val maxMM: Double? = null,
    val minFStop: Double? = null,
    val maxFStop: Double? = null,
    val lensName: String = ""
) : ICommand<Result<LensDto>>