// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/LocationExistsByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.location

data class LocationExistsByIdQuery(
    val id: Int
)

data class LocationExistsByIdQueryResult(
    val exists: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)