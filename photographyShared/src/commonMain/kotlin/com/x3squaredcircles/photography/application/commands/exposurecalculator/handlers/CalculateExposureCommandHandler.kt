// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/exposurecalculator/handlers/CalculateExposureCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.exposurecalculator.handlers

import com.x3squaredcircles.photography.application.commands.exposurecalculator.CalculateExposureCommand
import com.x3squaredcircles.photography.application.commands.exposurecalculator.CalculateExposureCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.domain.services.IExposureCalculatorService
import com.x3squaredcircles.photography.domain.models.FixedValue
import com.x3squaredcircles.photography.domain.models.ExposureSettingsDto
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class CalculateExposureCommandHandler(
    private val exposureCalculatorService: IExposureCalculatorService,
    private val logger: Logger
) : ICommandHandler<CalculateExposureCommand, CalculateExposureCommandResult> {

    override suspend fun handle(command: CalculateExposureCommand): Result<CalculateExposureCommandResult> {
        logger.d { "Handling CalculateExposureCommand with toCalculate: ${command.toCalculate}" }

        return try {
            // Validate base exposure
            if (command.baseExposure.shutterSpeed.isBlank() ||
                command.baseExposure.aperture.isBlank() ||
                command.baseExposure.iso.isBlank()) {
                return Result.success(
                    CalculateExposureCommandResult(
                        settings = ExposureSettingsDto("", "", ""),
                        isSuccess = false,
                        errorMessage = "Base exposure settings are required"
                    )
                )
            }

            // Validate EV compensation range
            if (command.evCompensation < -5.0 || command.evCompensation > 5.0) {
                return Result.success(
                    CalculateExposureCommandResult(
                        settings = ExposureSettingsDto("", "", ""),
                        isSuccess = false,
                        errorMessage = "EV compensation must be between -5 and +5 stops"
                    )
                )
            }

            // Perform calculations based on what's being calculated
            val calculationResult = when (command.toCalculate) {
                FixedValue.SHUTTER_SPEEDS -> {
                    if (command.targetAperture.isBlank() || command.targetIso.isBlank()) {
                        return Result.success(
                            CalculateExposureCommandResult(
                                settings = ExposureSettingsDto("", "", ""),
                                isSuccess = false,
                                errorMessage = "Target aperture and ISO are required for shutter speed calculation"
                            )
                        )
                    }

                    exposureCalculatorService.calculateShutterSpeedAsync(
                        command.baseExposure,
                        command.targetAperture,
                        command.targetIso,
                        command.increments,
                        command.evCompensation
                    )
                }

                FixedValue.APERTURE -> {
                    if (command.targetShutterSpeed.isBlank() || command.targetIso.isBlank()) {
                        return Result.success(
                            CalculateExposureCommandResult(
                                settings = ExposureSettingsDto("", "", ""),
                                isSuccess = false,
                                errorMessage = "Target shutter speed and ISO are required for aperture calculation"
                            )
                        )
                    }

                    exposureCalculatorService.calculateApertureAsync(
                        command.baseExposure,
                        command.targetShutterSpeed,
                        command.targetIso,
                        command.increments,
                        command.evCompensation
                    )
                }

                FixedValue.ISO -> {
                    if (command.targetShutterSpeed.isBlank() || command.targetAperture.isBlank()) {
                        return Result.success(
                            CalculateExposureCommandResult(
                                settings = ExposureSettingsDto("", "", ""),
                                isSuccess = false,
                                errorMessage = "Target shutter speed and aperture are required for ISO calculation"
                            )
                        )
                    }

                    exposureCalculatorService.calculateIsoAsync(
                        command.baseExposure,
                        command.targetShutterSpeed,
                        command.targetAperture,
                        command.increments,
                        command.evCompensation
                    )
                }

                else -> {
                    return Result.success(
                        CalculateExposureCommandResult(
                            settings = ExposureSettingsDto("", "", ""),
                            isSuccess = false,
                            errorMessage = "Invalid calculation type"
                        )
                    )
                }
            }

            when (calculationResult) {
                is Result.Success -> {
                    logger.i { "Successfully calculated exposure: ${calculationResult.data}" }
                    Result.success(
                        CalculateExposureCommandResult(
                            settings = calculationResult.data,
                            isSuccess = true
                        )
                    )
                }
                is Result.Failure -> {
                    logger.w { "Exposure calculation failed: ${calculationResult.error}" }
                    Result.success(
                        CalculateExposureCommandResult(
                            settings = ExposureSettingsDto("", "", ""),
                            isSuccess = false,
                            errorMessage = calculationResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating exposure" }
            Result.success(
                CalculateExposureCommandResult(
                    settings = ExposureSettingsDto("", "", ""),
                    isSuccess = false,
                    errorMessage = "Error calculating exposure: ${ex.message}"
                )
            )
        }
    }
}