// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/camerabody/CreateCameraBodyCommand.kt
package com.x3squaredcircles.photography.application.commands.camerabody

import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.domain.enums.MountType

data class CreateCameraBodyCommand(
    val name: String,
    val sensorType: String,
    val sensorWidth: Double,
    val sensorHeight: Double,
    val mountType: MountType,
    val isUserCreated: Boolean = true
)

data class CreateCameraBodyCommandResult(
    val cameraBody: CameraBodyDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)