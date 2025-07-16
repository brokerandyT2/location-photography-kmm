// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/GetPagedLensesQuery.kt
package com.x3squaredcircles.photography.application.queries.lens

data class GetPagedLensesQuery(
    val pageSize: Int,
    val offset: Int
)

data class GetPagedLensesQueryResult(
    val lenses: List<LensDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)