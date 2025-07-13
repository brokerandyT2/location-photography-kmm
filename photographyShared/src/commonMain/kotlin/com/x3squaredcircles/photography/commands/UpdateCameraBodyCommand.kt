// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/commands/UpdateCameraBodyCommand.kt
package com.x3squaredcircles.photography.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand
import com.x3squaredcircles.photography.dtos.CameraBodyDto
import com.x3squaredcircles.photography.domain.enums.MountType

data class UpdateCameraBodyCommand(
    val id: Int,
    val name: String = "",
    val sensorType: String = "",
    val sensorWidth: Double = 0.0,
    val sensorHeight: Double = 0.0,
    val mountType: MountType = MountType.Other
) : ICommand<Result<CameraBodyDto>>