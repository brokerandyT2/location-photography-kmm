// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/GetAllLensesQuery.kt
package com.x3squaredcircles.photography.application.queries.lens

data class GetAllLensesQuery(
    val dummy: Boolean = true
)

data class GetAllLensesQueryResult(
    val lenses: List<LensDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class LensDto(
    val id: Int,
    val minMM: Double,
    val maxMM: Double,
    val minFStop: Double,
    val maxFStop: Double,
    val isPrime: Boolean,
    val isUserCreated: Boolean,
    val nameForLens: String,
    val dateAdded: Long
)