// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/GetRecentLocationsQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.core.domain.entities.Location

data class GetRecentLocationsQuery(
    val count: Int = 10
)

data class GetRecentLocationsQueryResult(
    val locations: List<Location>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)