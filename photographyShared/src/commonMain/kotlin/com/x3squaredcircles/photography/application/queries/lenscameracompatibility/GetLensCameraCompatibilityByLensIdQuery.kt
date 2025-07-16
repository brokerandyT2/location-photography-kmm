// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/GetLensCameraCompatibilityByLensIdQuery.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility

data class GetLensCameraCompatibilityByLensIdQuery(
    val lensId: Int
)

data class GetLensCameraCompatibilityByLensIdQueryResult(
    val compatibilities: List<LensCameraCompatibilityDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)