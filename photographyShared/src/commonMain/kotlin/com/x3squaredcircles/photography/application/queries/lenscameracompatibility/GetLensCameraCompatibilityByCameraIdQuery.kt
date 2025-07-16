// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/GetLensCameraCompatibilityByCameraIdQuery.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility

data class GetLensCameraCompatibilityByCameraIdQuery(
    val cameraBodyId: Int
)

data class GetLensCameraCompatibilityByCameraIdQueryResult(
    val compatibilities: List<LensCameraCompatibilityDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)