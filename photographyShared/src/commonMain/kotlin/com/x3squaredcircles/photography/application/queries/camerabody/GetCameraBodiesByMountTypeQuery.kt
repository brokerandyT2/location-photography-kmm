// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/GetCameraBodiesByMountTypeQuery.kt
package com.x3squaredcircles.photography.application.queries.camerabody

data class GetCameraBodiesByMountTypeQuery(
    val mountType: String
)

data class GetCameraBodiesByMountTypeQueryResult(
    val cameraBodies: List<CameraBodyDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)