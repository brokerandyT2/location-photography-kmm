// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/GetPagedLocationsQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.core.domain.entities.Location

data class GetPagedLocationsQuery(
    val pageNumber: Int,
    val pageSize: Int,
    val includeDeleted: Boolean = false,
    val searchTerm: String? = null
)

data class GetPagedLocationsQueryResult(
    val locations: List<Location>,
    val totalCount: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)