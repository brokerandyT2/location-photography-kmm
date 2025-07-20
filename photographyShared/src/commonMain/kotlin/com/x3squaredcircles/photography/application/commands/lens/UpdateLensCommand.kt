// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/lens/UpdateLensCommand.kt
package com.x3squaredcircles.photography.application.commands.lens

import com.x3squaredcircles.photography.application.queries.lens.LensDto

data class UpdateLensCommand(
    val id: Int,
    val minMM: Double,
    val maxMM: Double?,
    val minFStop: Double?,
    val maxFStop: Double?,
    val lensName: String,
    val compatibleCameraIds: List<Int>
)

data class UpdateLensCommandResult(
    val lens: LensDto,
    val compatibleCameraIds: List<Int>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)