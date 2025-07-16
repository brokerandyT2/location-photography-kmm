// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/GetLensCameraCompatibilityByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility

data class GetLensCameraCompatibilityByIdQuery(
    val id: Int
)

data class GetLensCameraCompatibilityByIdQueryResult(
    val compatibility: LensCameraCompatibilityDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)