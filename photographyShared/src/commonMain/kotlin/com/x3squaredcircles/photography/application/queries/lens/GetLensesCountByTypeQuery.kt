// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/GetLensesCountByTypeQuery.kt
package com.x3squaredcircles.photography.application.queries.lens

data class GetLensesCountByTypeQuery(
    val dummy: Boolean = true
)

data class GetLensesCountByTypeQueryResult(
    val primeCount: Long,
    val zoomCount: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)