// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/SearchCameraBodiesByNameQuery.kt
package com.x3squaredcircles.photography.application.queries.camerabody

data class SearchCameraBodiesByNameQuery(
    val searchTerm: String
)

data class SearchCameraBodiesByNameQueryResult(
    val cameraBodies: List<CameraBodyDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)