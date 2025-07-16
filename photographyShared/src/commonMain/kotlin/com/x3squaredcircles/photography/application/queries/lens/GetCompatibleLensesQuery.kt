// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/GetCompatibleLensesQuery.kt
package com.x3squaredcircles.photography.application.queries.lens

data class GetCompatibleLensesQuery(
    val cameraBodyId: Int
)

data class GetCompatibleLensesQueryResult(
    val lenses: List<LensDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)