// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/UpdateLocationCoordinatesCommand.kt
package com.x3squaredcircles.photography.application.commands.location

data class UpdateLocationCoordinatesCommand(
    val id: Int,
    val latitude: Double,
    val longitude: Double
)

data class UpdateLocationCoordinatesCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)