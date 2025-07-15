// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/GetLocationsByBoundsQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.core.domain.entities.Location

data class GetLocationsByBoundsQuery(
    val southLatitude: Double,
    val northLatitude: Double,
    val westLongitude: Double,
    val eastLongitude: Double
)

data class GetLocationsByBoundsQueryResult(
    val locations: List<Location>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)