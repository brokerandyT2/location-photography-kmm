// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/LensCameraCompatibilityExistsQuery.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility

data class LensCameraCompatibilityExistsQuery(
    val lensId: Int,
    val cameraBodyId: Int
)

data class LensCameraCompatibilityExistsQueryResult(
    val exists: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)