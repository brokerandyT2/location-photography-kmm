// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/GetLocationsModifiedSinceQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.core.domain.entities.Location

data class GetLocationsModifiedSinceQuery(
    val timestamp: Long
)

data class GetLocationsModifiedSinceQueryResult(
    val locations: List<Location>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)