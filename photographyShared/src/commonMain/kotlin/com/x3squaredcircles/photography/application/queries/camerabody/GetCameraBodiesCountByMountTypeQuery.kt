// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/GetCameraBodiesCountByMountTypeQuery.kt
package com.x3squaredcircles.photography.application.queries.camerabody

data class GetCameraBodiesCountByMountTypeQuery(
    val dummy: Boolean = true
)

data class GetCameraBodiesCountByMountTypeQueryResult(
    val countByMountType: Map<String, Long>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)