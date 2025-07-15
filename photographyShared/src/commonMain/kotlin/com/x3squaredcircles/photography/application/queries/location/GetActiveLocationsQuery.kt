// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/GetActiveLocationsQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.core.domain.entities.Location

data class GetActiveLocationsQuery(
    val dummy: Boolean = true
)

data class GetActiveLocationsQueryResult(
    val locations: List<Location>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)