// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/GetAllLocationsQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.core.domain.entities.Location

data class GetAllLocationsQuery(
    val includeDeleted: Boolean = false
)

data class GetAllLocationsQueryResult(
    val locations: List<Location>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)