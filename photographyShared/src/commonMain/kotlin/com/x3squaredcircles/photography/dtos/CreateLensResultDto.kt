// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/dtos/CreateLensResultDto.kt
package com.x3squaredcircles.photography.dtos

data class CreateLensResultDto(
    val lens: LensDto? = null,
    val compatibleCameraIds: List<Int> = emptyList(),
    val isSuccessful: Boolean = false,
    val errorMessage: String = ""
)