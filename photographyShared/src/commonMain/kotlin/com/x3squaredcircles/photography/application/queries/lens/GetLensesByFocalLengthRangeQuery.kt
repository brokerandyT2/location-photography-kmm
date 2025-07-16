// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/GetLensesByFocalLengthRangeQuery.kt
package com.x3squaredcircles.photography.application.queries.lens

data class GetLensesByFocalLengthRangeQuery(
    val minFocalLength: Double,
    val maxFocalLength: Double
)

data class GetLensesByFocalLengthRangeQueryResult(
    val lenses: List<LensDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)