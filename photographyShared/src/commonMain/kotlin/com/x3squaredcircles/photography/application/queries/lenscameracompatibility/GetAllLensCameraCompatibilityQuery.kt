// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/GetAllLensCameraCompatibilityQuery.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility

data class GetAllLensCameraCompatibilityQuery(
    val dummy: Boolean = true
)

data class GetAllLensCameraCompatibilityQueryResult(
    val compatibilities: List<LensCameraCompatibilityDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class LensCameraCompatibilityDto(
    val id: Int,
    val lensId: Int,
    val cameraBodyId: Int,
    val dateAdded: Long
)