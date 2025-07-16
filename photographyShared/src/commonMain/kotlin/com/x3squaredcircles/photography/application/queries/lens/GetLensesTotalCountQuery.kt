// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/GetLensesTotalCountQuery.kt
package com.x3squaredcircles.photography.application.queries.lens

data class GetLensesTotalCountQuery(
    val dummy: Boolean = true
)

data class GetLensesTotalCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)