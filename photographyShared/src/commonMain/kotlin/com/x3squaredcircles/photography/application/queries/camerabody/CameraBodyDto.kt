// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/CameraBodyDto.kt
package com.x3squaredcircles.photography.application.queries.camerabody

data class CameraBodyDto(
    val id: Int,
    val name: String,
    val sensorType: String,
    val sensorWidth: Double,
    val sensorHeight: Double,
    val mountType: String,
    val isUserCreated: Boolean,
    val dateAdded: Long,
    val displayName: String
)