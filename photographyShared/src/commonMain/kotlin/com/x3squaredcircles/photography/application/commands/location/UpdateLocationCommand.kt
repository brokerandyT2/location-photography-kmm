// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/UpdateLocationCommand.kt
package com.x3squaredcircles.photography.application.commands.location

import com.x3squaredcircles.core.domain.entities.Location

data class UpdateLocationCommand(
    val id: Int,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val city: String = "",
    val state: String = "",
    val photoPath: String? = null
)

data class UpdateLocationCommandResult(
    val location: Location?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)