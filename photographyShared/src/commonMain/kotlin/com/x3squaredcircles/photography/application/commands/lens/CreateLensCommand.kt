// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/lens/CreateLensCommand.kt
package com.x3squaredcircles.photography.application.commands.lens

import com.x3squaredcircles.photography.application.queries.lens.LensDto
import com.x3squaredcircles.photography.domain.enums.MountType

data class CreateLensCommand(
    val minMM: Double,
    val maxMM: Double?,
    val minFStop: Double?,
    val maxFStop: Double?,
    val isUserCreated: Boolean = true,
    val lensName: String,
    val compatibleCameraIds: List<Int>
)

data class CreateLensCommandResult(
    val lens: LensDto,
    val compatibleCameraIds: List<Int>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)