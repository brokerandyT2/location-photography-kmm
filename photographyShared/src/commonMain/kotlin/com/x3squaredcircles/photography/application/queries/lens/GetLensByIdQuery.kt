// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/GetLensByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.lens

data class GetLensByIdQuery(
    val id: Int
)

data class GetLensByIdQueryResult(
    val lens: LensDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)