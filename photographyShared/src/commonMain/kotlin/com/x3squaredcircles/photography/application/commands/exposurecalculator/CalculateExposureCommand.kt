// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/exposurecalculator/CalculateExposureCommand.kt
package com.x3squaredcircles.photography.application.commands.exposurecalculator

import com.x3squaredcircles.photography.domain.models.ExposureTriangleDto
import com.x3squaredcircles.photography.domain.models.ExposureSettingsDto
import com.x3squaredcircles.photography.domain.models.ExposureIncrements
import com.x3squaredcircles.photography.domain.models.FixedValue

data class CalculateExposureCommand(
    val baseExposure: ExposureTriangleDto,
    val targetAperture: String = "",
    val targetShutterSpeed: String = "",
    val targetIso: String = "",
    val increments: ExposureIncrements,
    val toCalculate: FixedValue,
    val evCompensation: Double = 0.0
)

data class CalculateExposureCommandResult(
    val settings: ExposureSettingsDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)