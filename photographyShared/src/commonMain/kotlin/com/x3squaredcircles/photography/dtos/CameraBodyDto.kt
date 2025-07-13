// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/CameraBodyDto.kt
package com.x3squaredcircles.photography.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CameraBodyDto(
    val id: Int = 0,
    val name: String = "",
    val sensorType: String = "",
    val sensorWidth: Double = 0.0,
    val sensorHeight: Double = 0.0,
    val mountType: String = "",
    val isUserCreated: Boolean = false,
    val dateAdded: Long = 0L,
    val displayName: String = ""
)