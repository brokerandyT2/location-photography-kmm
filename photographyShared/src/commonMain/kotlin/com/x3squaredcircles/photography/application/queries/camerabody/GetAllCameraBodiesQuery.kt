// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/GetAllCameraBodiesQuery.kt
package com.x3squaredcircles.photography.application.queries.camerabody

data class GetAllCameraBodiesQuery(
    val dummy: Boolean = true
)

data class GetAllCameraBodiesQueryResult(
    val cameraBodies: List<CameraBodyDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

