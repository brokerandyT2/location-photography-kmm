// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/IExposureCalculatorService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.models.ExposureIncrements
import com.x3squaredcircles.photography.domain.models.ExposureSettingsDto
import com.x3squaredcircles.photography.domain.models.ExposureTriangleDto

interface IExposureCalculatorService {

    suspend fun calculateShutterSpeedAsync(
        baseExposure: ExposureTriangleDto,
        targetAperture: String,
        targetIso: String,
        increments: ExposureIncrements,
        evCompensation: Double = 0.0
    ): Result<ExposureSettingsDto>

    suspend fun calculateApertureAsync(
        baseExposure: ExposureTriangleDto,
        targetShutterSpeed: String,
        targetIso: String,
        increments: ExposureIncrements,
        evCompensation: Double = 0.0
    ): Result<ExposureSettingsDto>

    suspend fun calculateIsoAsync(
        baseExposure: ExposureTriangleDto,
        targetShutterSpeed: String,
        targetAperture: String,
        increments: ExposureIncrements,
        evCompensation: Double = 0.0
    ): Result<ExposureSettingsDto>

    suspend fun getShutterSpeedsAsync(
        increments: ExposureIncrements
    ): Result<Array<String>>

    suspend fun getAperturesAsync(
        increments: ExposureIncrements
    ): Result<Array<String>>

    suspend fun getIsosAsync(
        increments: ExposureIncrements
    ): Result<Array<String>>
}