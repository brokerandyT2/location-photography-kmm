// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/GetLocationStatsQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.LocationStats

data class GetLocationStatsQuery(
    val dummy: Boolean = true
)

data class GetLocationStatsQueryResult(
    val stats: LocationStats,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)