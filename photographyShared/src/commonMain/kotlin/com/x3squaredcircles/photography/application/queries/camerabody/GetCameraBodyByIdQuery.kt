// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/GetCameraBodyByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.camerabody

data class GetCameraBodyByIdQuery(
    val id: Int
)

data class GetCameraBodyByIdQueryResult(
    val cameraBody: CameraBodyDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)