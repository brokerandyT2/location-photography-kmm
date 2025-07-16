// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/GetCameraBodiesTotalCountQuery.kt
package com.x3squaredcircles.photography.application.queries.camerabody

data class GetCameraBodiesTotalCountQuery(
    val dummy: Boolean = true
)

data class GetCameraBodiesTotalCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)