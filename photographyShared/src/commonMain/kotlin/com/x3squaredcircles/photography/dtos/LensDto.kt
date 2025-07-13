// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/LensDto.kt
package com.x3squaredcircles.photography.dtos

import kotlinx.serialization.Serializable

@Serializable
data class LensDto(
    val id: Int = 0,
    val minMM: Double = 0.0,
    val maxMM: Double = 0.0,
    val minFStop: Double = 0.0,
    val maxFStop: Double = 0.0,
    val isPrime: Boolean = false,
    val isUserCreated: Boolean = false,
    val nameForLens: String = "",
    val dateAdded: Long = 0L,
    val displayName: String = ""
)