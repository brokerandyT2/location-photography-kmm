// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/BulkUpdateLocationsCommand.kt
package com.x3squaredcircles.photography.application.commands.location

data class BulkUpdateLocationsCommand(
    val locations: List<LocationUpdateData>
)

data class LocationUpdateData(
    val id: Int,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val city: String = "",
    val state: String = "",
    val photoPath: String? = null
)

data class BulkUpdateLocationsCommandResult(
    val updatedCount: Int,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)