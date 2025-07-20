// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/camerabody/UpdateCameraBodyCommand.kt
package com.x3squaredcircles.photography.application.commands.camerabody

import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.domain.enums.MountType

data class UpdateCameraBodyCommand(
    val id: Int,
    val name: String,
    val sensorType: String,
    val sensorWidth: Double,
    val sensorHeight: Double,
    val mountType: MountType
)

data class UpdateCameraBodyCommandResult(
    val cameraBody: CameraBodyDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)