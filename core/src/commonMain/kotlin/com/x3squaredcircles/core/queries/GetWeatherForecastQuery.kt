// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetWeatherForecastQuery.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQuery
import com.x3squaredcircles.core.dtos.WeatherDto
import kotlinx.serialization.Serializable

/**
 * Query to retrieve weather forecast for a specific location.
 */
@Serializable
data class GetWeatherForecastQuery(
    val locationId: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val forceRefresh: Boolean = false
) : IQuery<Result<WeatherDto>> {
    
    init {
        require((locationId != null) || (latitude != null && longitude != null)) {
            "Either locationId or coordinates (latitude and longitude) must be provided"
        }
        
        if (latitude != null) {
            require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90 degrees" }
        }
        
        if (longitude != null) {
            require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180 degrees" }
        }
    }
}