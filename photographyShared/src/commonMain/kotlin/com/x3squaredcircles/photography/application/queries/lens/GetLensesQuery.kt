// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/GetLensesQuery.kt
package com.x3squaredcircles.photography.application.queries.lens

data class GetLensesQuery(
    val skip: Int = 0,
    val take: Int = 20,
    val userLensesOnly: Boolean = false,
    val compatibleWithCameraId: Int? = null
)

data class GetLensesQueryResult(
    val lenses: List<LensDto>,
    val totalCount: Int,
    val hasMore: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)