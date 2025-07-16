// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/GetUserCreatedCameraBodiesQuery.kt
package com.x3squaredcircles.photography.application.queries.camerabody

data class GetUserCreatedCameraBodiesQuery(
    val dummy: Boolean = true
)

data class GetUserCreatedCameraBodiesQueryResult(
    val cameraBodies: List<CameraBodyDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)