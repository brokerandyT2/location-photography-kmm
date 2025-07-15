// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/UpdateLocationPhotoCommand.kt
package com.x3squaredcircles.photography.application.commands.location

data class UpdateLocationPhotoCommand(
    val id: Int,
    val photoPath: String?
)

data class UpdateLocationPhotoCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)