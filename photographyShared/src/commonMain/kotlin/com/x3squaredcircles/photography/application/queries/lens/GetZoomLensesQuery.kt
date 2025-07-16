// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/GetZoomLensesQuery.kt
package com.x3squaredcircles.photography.application.queries.lens

data class GetZoomLensesQuery(
    val dummy: Boolean = true
)

data class GetZoomLensesQueryResult(
    val lenses: List<LensDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)