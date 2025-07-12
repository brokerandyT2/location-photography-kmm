// core/src/commonMain/kotlin/com/x3squaredcircles/core/queries/GetLocationsQuery.kt
package com.x3squaredcircles.core.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQuery
import com.x3squaredcircles.core.models.PagedList
import com.x3squaredcircles.core.dtos.LocationListDto

import kotlinx.serialization.Serializable

/**
 * Query to retrieve a paged list of locations with optional filtering.
 */
@Serializable
data class GetLocationsQuery(
    val pageNumber: Int = 1,
    val pageSize: Int = 10,
    val searchTerm: String? = null,
    val includeDeleted: Boolean = false
) : IQuery<Result<PagedList<LocationListDto>>> {
    
    init {
        require(pageNumber > 0) { "Page number must be greater than 0" }
        require(pageSize > 0) { "Page size must be greater than 0" }
        require(pageSize <= 100) { "Page size cannot exceed 100" }
    }
}