// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/GetRandomLocationQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.core.domain.entities.Location

data class GetRandomLocationQuery(
    val dummy: Boolean = true
)

data class GetRandomLocationQueryResult(
    val location: Location?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)