// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/SearchLocationsByTextQuery.kt
package com.x3squaredcircles.photography.application.queries.location

import com.x3squaredcircles.core.domain.entities.Location

data class SearchLocationsByTextQuery(
    val searchTerm: String,
    val includeDeleted: Boolean = false
)

data class SearchLocationsByTextQueryResult(
    val locations: List<Location>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)