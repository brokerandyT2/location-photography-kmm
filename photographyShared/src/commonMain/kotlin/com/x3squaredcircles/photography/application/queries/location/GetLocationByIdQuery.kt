// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/GetLocationByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.core.domain.entities.Location

data class GetLocationByIdQuery(
    val id: Int
)

data class GetLocationByIdQueryResult(
    val location: Location?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)