// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/exposurecalculator/validators/CalculateExposureCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.exposurecalculator.validators

import com.x3squaredcircles.photography.application.commands.exposurecalculator.CalculateExposureCommand
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult
import com.x3squaredcircles.photography.application.common.behaviors.ValidationError
import com.x3squaredcircles.photography.domain.models.FixedValue
import com.x3squaredcircles.photography.domain.models.ExposureIncrements

class CalculateExposureCommandValidator : IValidator<CalculateExposureCommand> {

    companion object {
        private const val MIN_EV_COMPENSATION = -5.0
        private const val MAX_EV_COMPENSATION = 5.0
        private const val MIN_ISO = 25
        private const val MAX_ISO = 102400
        private const val MIN_APERTURE = 0.7
        private const val MAX_APERTURE = 64.0
        private const val MIN_SHUTTER_SPEED = 0.000125 // 1/8000
        private const val MAX_SHUTTER_SPEED = 3600.0 // 1 hour
    }

    override suspend fun validate(request: CalculateExposureCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Validate BaseExposure
        if (isBaseExposureInvalid(request)) {
            errors.add(
                ValidationError(
                    propertyName = "baseExposure",
                    errorMessage = "Base exposure settings are required",
                    attemptedValue = request.baseExposure
                )
            )
        } else {
            // Validate individual base exposure components
            validateBaseExposureComponents(request, errors)
        }

        // Validate Increments enum
        validateExposureIncrements(request.increments, errors)

        // Validate ToCalculate enum
        validateFixedValue(request.toCalculate, errors)

        // Validate EV Compensation
        if (request.evCompensation < MIN_EV_COMPENSATION || request.evCompensation > MAX_EV_COMPENSATION) {
            errors.add(
                ValidationError(
                    propertyName = "evCompensation",
                    errorMessage = "EV compensation must be between $MIN_EV_COMPENSATION and $MAX_EV_COMPENSATION stops",
                    attemptedValue = request.evCompensation
                )
            )
        }

        // Validate target values based on calculation type
        validateTargetValues(request, errors)

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }

    private fun isBaseExposureInvalid(request: CalculateExposureCommand): Boolean {
        return request.baseExposure.shutterSpeed.isBlank() ||
                request.baseExposure.aperture.isBlank() ||
                request.baseExposure.iso.isBlank()
    }

    private fun validateBaseExposureComponents(request: CalculateExposureCommand, errors: MutableList<ValidationError>) {
        // Validate base shutter speed
        if (request.baseExposure.shutterSpeed.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "baseExposure.shutterSpeed",
                    errorMessage = "Base shutter speed is required",
                    attemptedValue = request.baseExposure.shutterSpeed
                )
            )
        } else if (!isValidShutterSpeed(request.baseExposure.shutterSpeed)) {
            errors.add(
                ValidationError(
                    propertyName = "baseExposure.shutterSpeed",
                    errorMessage = "Base shutter speed must be in valid format (e.g., 1/125, 2\", 0.5)",
                    attemptedValue = request.baseExposure.shutterSpeed
                )
            )
        }

        // Validate base aperture
        if (request.baseExposure.aperture.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "baseExposure.aperture",
                    errorMessage = "Base aperture is required",
                    attemptedValue = request.baseExposure.aperture
                )
            )
        } else if (!isValidAperture(request.baseExposure.aperture)) {
            errors.add(
                ValidationError(
                    propertyName = "baseExposure.aperture",
                    errorMessage = "Base aperture must be in valid f-stop format (e.g., f/2.8)",
                    attemptedValue = request.baseExposure.aperture
                )
            )
        }

        // Validate base ISO
        if (request.baseExposure.iso.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "baseExposure.iso",
                    errorMessage = "Base ISO is required",
                    attemptedValue = request.baseExposure.iso
                )
            )
        } else if (!isValidIso(request.baseExposure.iso)) {
            errors.add(
                ValidationError(
                    propertyName = "baseExposure.iso",
                    errorMessage = "Base ISO must be a valid numeric value (e.g., 100, 400, 1600)",
                    attemptedValue = request.baseExposure.iso
                )
            )
        }
    }

    private fun validateTargetValues(request: CalculateExposureCommand, errors: MutableList<ValidationError>) {
        when (request.toCalculate) {
            FixedValue.SHUTTER_SPEEDS -> {
                // Target aperture and ISO required for shutter speed calculation
                if (request.targetAperture.isBlank()) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetAperture",
                            errorMessage = "Target aperture is required",
                            attemptedValue = request.targetAperture
                        )
                    )
                } else if (!isValidAperture(request.targetAperture)) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetAperture",
                            errorMessage = "Target aperture must be in valid f-stop format (e.g., f/2.8)",
                            attemptedValue = request.targetAperture
                        )
                    )
                }

                if (request.targetIso.isBlank()) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetIso",
                            errorMessage = "Target ISO is required",
                            attemptedValue = request.targetIso
                        )
                    )
                } else if (!isValidIso(request.targetIso)) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetIso",
                            errorMessage = "Target ISO must be a valid numeric value (e.g., 100, 400, 1600)",
                            attemptedValue = request.targetIso
                        )
                    )
                }
            }

            FixedValue.APERTURE -> {
                // Target shutter speed and ISO required for aperture calculation
                if (request.targetShutterSpeed.isBlank()) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetShutterSpeed",
                            errorMessage = "Target shutter speed is required",
                            attemptedValue = request.targetShutterSpeed
                        )
                    )
                } else if (!isValidShutterSpeed(request.targetShutterSpeed)) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetShutterSpeed",
                            errorMessage = "Target shutter speed must be in valid format (e.g., 1/125, 2\", 0.5)",
                            attemptedValue = request.targetShutterSpeed
                        )
                    )
                }

                if (request.targetIso.isBlank()) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetIso",
                            errorMessage = "Target ISO is required",
                            attemptedValue = request.targetIso
                        )
                    )
                } else if (!isValidIso(request.targetIso)) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetIso",
                            errorMessage = "Target ISO must be a valid numeric value (e.g., 100, 400, 1600)",
                            attemptedValue = request.targetIso
                        )
                    )
                }
            }

            FixedValue.ISO -> {
                // Target shutter speed and aperture required for ISO calculation
                if (request.targetShutterSpeed.isBlank()) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetShutterSpeed",
                            errorMessage = "Target shutter speed is required",
                            attemptedValue = request.targetShutterSpeed
                        )
                    )
                } else if (!isValidShutterSpeed(request.targetShutterSpeed)) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetShutterSpeed",
                            errorMessage = "Target shutter speed must be in valid format (e.g., 1/125, 2\", 0.5)",
                            attemptedValue = request.targetShutterSpeed
                        )
                    )
                }

                if (request.targetAperture.isBlank()) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetAperture",
                            errorMessage = "Target aperture is required",
                            attemptedValue = request.targetAperture
                        )
                    )
                } else if (!isValidAperture(request.targetAperture)) {
                    errors.add(
                        ValidationError(
                            propertyName = "targetAperture",
                            errorMessage = "Target aperture must be in valid f-stop format (e.g., f/2.8)",
                            attemptedValue = request.targetAperture
                        )
                    )
                }
            }

            FixedValue.EMPTY -> TODO()
        }
    }

    private fun isValidShutterSpeed(shutterSpeed: String): Boolean {
        if (shutterSpeed.isBlank()) return false

        return try {
            when {
                // Handle fractional shutter speeds like "1/125"
                shutterSpeed.contains('/') -> {
                    val parts = shutterSpeed.split('/')
                    if (parts.size != 2) return false

                    val numerator = parts[0].toDoubleOrNull() ?: return false
                    val denominator = parts[1].toDoubleOrNull() ?: return false

                    if (denominator <= 0) return false
                    val speed = numerator / denominator
                    speed in MIN_SHUTTER_SPEED..MAX_SHUTTER_SPEED
                }

                // Handle speeds with seconds mark like '30"'
                shutterSpeed.endsWith('"') -> {
                    val value = shutterSpeed.dropLast(1).toDoubleOrNull() ?: return false
                    value > 0 && value <= MAX_SHUTTER_SPEED
                }

                // Handle regular decimal values
                else -> {
                    val value = shutterSpeed.toDoubleOrNull() ?: return false
                    value > 0 && value <= MAX_SHUTTER_SPEED
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun isValidAperture(aperture: String): Boolean {
        if (aperture.isBlank()) return false

        return try {
            val value = when {
                // Handle f-stop format like "f/2.8"
                aperture.startsWith("f/", ignoreCase = true) -> {
                    aperture.substring(2).toDoubleOrNull() ?: return false
                }
                // Handle raw numbers
                else -> {
                    aperture.toDoubleOrNull() ?: return false
                }
            }

            value in MIN_APERTURE..MAX_APERTURE
        } catch (e: Exception) {
            false
        }
    }

    private fun isValidIso(iso: String): Boolean {
        if (iso.isBlank()) return false

        return try {
            val value = iso.toIntOrNull() ?: return false
            value in MIN_ISO..MAX_ISO
        } catch (e: Exception) {
            false
        }
    }

    private fun validateExposureIncrements(increments: ExposureIncrements, errors: MutableList<ValidationError>) {
        try {
            // Enum validation - if we can access it, it's valid
            val isValid = ExposureIncrements.values().contains(increments)
            if (!isValid) {
                errors.add(
                    ValidationError(
                        propertyName = "increments",
                        errorMessage = "Invalid exposure increment value",
                        attemptedValue = increments
                    )
                )
            }
        } catch (e: Exception) {
            errors.add(
                ValidationError(
                    propertyName = "increments",
                    errorMessage = "Invalid exposure increment value",
                    attemptedValue = increments
                )
            )
        }
    }

    private fun validateFixedValue(fixedValue: FixedValue, errors: MutableList<ValidationError>) {
        try {
            // Enum validation - if we can access it, it's valid
            val isValid = FixedValue.values().contains(fixedValue)
            if (!isValid) {
                errors.add(
                    ValidationError(
                        propertyName = "toCalculate",
                        errorMessage = "Invalid calculation type",
                        attemptedValue = fixedValue
                    )
                )
            }
        } catch (e: Exception) {
            errors.add(
                ValidationError(
                    propertyName = "toCalculate",
                    errorMessage = "Invalid calculation type",
                    attemptedValue = fixedValue
                )
            )
        }
    }
}