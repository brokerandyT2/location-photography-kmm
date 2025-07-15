// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/location/LocationExistsByTitleQuery.kt
package com.x3squaredcircles.photography.application.queries.location

data class LocationExistsByTitleQuery(
    val title: String,
    val excludeId: Int = 0
)

data class LocationExistsByTitleQueryResult(
    val exists: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)