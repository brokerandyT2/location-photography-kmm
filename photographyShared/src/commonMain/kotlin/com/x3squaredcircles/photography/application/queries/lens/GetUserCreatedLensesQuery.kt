// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/GetUserCreatedLensesQuery.kt
package com.x3squaredcircles.photography.application.queries.lens

data class GetUserCreatedLensesQuery(
    val dummy: Boolean = true
)

data class GetUserCreatedLensesQueryResult(
    val lenses: List<LensDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)