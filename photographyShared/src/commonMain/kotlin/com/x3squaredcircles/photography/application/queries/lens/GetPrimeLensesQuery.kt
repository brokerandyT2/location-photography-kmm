// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/GetPrimeLensesQuery.kt
package com.x3squaredcircles.photography.application.queries.lens

data class GetPrimeLensesQuery(
    val dummy: Boolean = true
)

data class GetPrimeLensesQueryResult(
    val lenses: List<LensDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)