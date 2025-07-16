// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/GetPagedCameraBodiesQuery.kt
package com.x3squaredcircles.photography.application.queries.camerabody

data class GetPagedCameraBodiesQuery(
    val pageSize: Int,
    val offset: Int
)

data class GetPagedCameraBodiesQueryResult(
    val cameraBodies: List<CameraBodyDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)