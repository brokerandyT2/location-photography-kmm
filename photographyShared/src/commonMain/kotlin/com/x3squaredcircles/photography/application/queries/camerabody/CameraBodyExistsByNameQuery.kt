// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/CameraBodyExistsByNameQuery.kt
package com.x3squaredcircles.photography.application.queries.camerabody

data class CameraBodyExistsByNameQuery(
    val name: String,
    val excludeId: Int
)

data class CameraBodyExistsByNameQueryResult(
    val exists: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)