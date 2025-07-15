// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/GetNearbyLocationsQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.core.domain.entities.Location

data class GetNearbyLocationsQuery(
    val centerLatitude: Double,
    val centerLongitude: Double,
    val radiusKm: Double,
    val limit: Int = 50
)

data class GetNearbyLocationsQueryResult(
    val locations: List<Location>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)