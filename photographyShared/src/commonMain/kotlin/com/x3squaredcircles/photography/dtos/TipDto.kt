// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/TipDto.kt
package com.x3squaredcircles.photography.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TipDto(
    val id: Int,
    val tipTypeId: Int,
    val title: String,
    val content: String,
    val fstop: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val dateAdded: Long = 0L,
    val isUserCreated: Boolean = false
)