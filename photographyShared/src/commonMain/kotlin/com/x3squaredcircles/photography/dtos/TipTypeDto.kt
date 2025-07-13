// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/TipTypeDto.kt
package com.x3squaredcircles.photography.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TipTypeDto(
    val id: Int,
    val name: String,
    val description: String = "",
    val dateAdded: Long = 0L,
    val isUserCreated: Boolean = false
)