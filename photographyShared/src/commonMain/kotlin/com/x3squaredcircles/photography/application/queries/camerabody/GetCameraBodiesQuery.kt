// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/GetCameraBodiesQuery.kt
package com.x3squaredcircles.photography.application.queries.camerabody

data class GetCameraBodiesQuery(
    val skip: Int = 0,
    val take: Int = 20,
    val userCamerasOnly: Boolean = false
)

data class GetCameraBodiesQueryResult(
    val cameraBodies: List<CameraBodyDto>,
    val totalCount: Int,
    val hasMore: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)