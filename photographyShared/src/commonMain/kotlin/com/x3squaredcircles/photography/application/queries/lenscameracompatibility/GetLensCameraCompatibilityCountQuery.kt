// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/GetLensCameraCompatibilityCountQuery.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility

data class GetLensCameraCompatibilityCountQuery(
    val dummy: Boolean = true
)

data class GetLensCameraCompatibilityCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)