// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/CreateLocationCommand.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand
import com.x3squaredcircles.core.dtos.LocationDto
import kotlinx.serialization.Serializable

/**
 * Command to create a new location.
 */
@Serializable
data class CreateLocationCommand(
    val title: String,
    val description: String = "",
    val latitude: Double,
    val longitude: Double,
    val photoPath: String? = null
) : ICommand<Result<LocationDto>> {
    
    init {
        require(title.isNotBlank()) { "Location title cannot be blank" }
        require(title.length <= 100) { "Location title cannot exceed 100 characters" }
        require(description.length <= 500) { "Location description cannot exceed 500 characters" }
        require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90 degrees" }
        require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180 degrees" }
    }
}