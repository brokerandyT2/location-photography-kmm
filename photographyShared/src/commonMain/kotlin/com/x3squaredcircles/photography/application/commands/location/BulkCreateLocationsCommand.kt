// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/BulkCreateLocationsCommand.kt
package com.x3squaredcircles.photography.application.commands.location

import com.x3squaredcircles.core.domain.entities.Location

data class BulkCreateLocationsCommand(
    val locations: List<LocationData>
)

data class LocationData(
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val city: String = "",
    val state: String = "",
    val photoPath: String? = null
)

data class BulkCreateLocationsCommandResult(
    val createdLocations: List<Location>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)