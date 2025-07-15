// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/GetLocationByTitleQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.core.domain.entities.Location

data class GetLocationByTitleQuery(
    val title: String
)

data class GetLocationByTitleQueryResult(
    val location: Location?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)