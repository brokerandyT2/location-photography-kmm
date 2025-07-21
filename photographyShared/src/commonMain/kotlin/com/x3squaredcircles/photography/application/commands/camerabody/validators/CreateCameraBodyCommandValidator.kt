// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/camerabody/validators/CreateCameraBodyCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.camerabody.validators

import com.x3squaredcircles.photography.application.commands.camerabody.CreateCameraBodyCommand
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult
import com.x3squaredcircles.photography.application.common.behaviors.ValidationError
import com.x3squaredcircles.photography.domain.enums.MountType

class CreateCameraBodyCommandValidator : IValidator<CreateCameraBodyCommand> {

    companion object {
        private const val MIN_SENSOR_DIMENSION = 0.1 // mm
        private const val MAX_SENSOR_DIMENSION = 100.0 // mm (reasonable max for any sensor)
        private const val MAX_NAME_LENGTH = 100
        private const val MAX_SENSOR_TYPE_LENGTH = 50
    }

    override suspend fun validate(request: CreateCameraBodyCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Validate Name
        if (request.name.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "name",
                    errorMessage = "Name is required",
                    attemptedValue = request.name
                )
            )
        } else {
            if (request.name.length > MAX_NAME_LENGTH) {
                errors.add(
                    ValidationError(
                        propertyName = "name",
                        errorMessage = "Name cannot exceed $MAX_NAME_LENGTH characters",
                        attemptedValue = request.name
                    )
                )
            }

            if (containsInvalidCharacters(request.name)) {
                errors.add(
                    ValidationError(
                        propertyName = "name",
                        errorMessage = "Name contains invalid characters",
                        attemptedValue = request.name
                    )
                )
            }
        }

        // Validate SensorType
        if (request.sensorType.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "sensorType",
                    errorMessage = "Sensor type is required",
                    attemptedValue = request.sensorType
                )
            )
        } else if (request.sensorType.length > MAX_SENSOR_TYPE_LENGTH) {
            errors.add(
                ValidationError(
                    propertyName = "sensorType",
                    errorMessage = "Sensor type cannot exceed $MAX_SENSOR_TYPE_LENGTH characters",
                    attemptedValue = request.sensorType
                )
            )
        }

        // Validate SensorWidth
        if (request.sensorWidth <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "sensorWidth",
                    errorMessage = "Sensor width must be greater than 0",
                    attemptedValue = request.sensorWidth
                )
            )
        } else {
            if (request.sensorWidth < MIN_SENSOR_DIMENSION) {
                errors.add(
                    ValidationError(
                        propertyName = "sensorWidth",
                        errorMessage = "Sensor width must be at least $MIN_SENSOR_DIMENSION mm",
                        attemptedValue = request.sensorWidth
                    )
                )
            }

            if (request.sensorWidth > MAX_SENSOR_DIMENSION) {
                errors.add(
                    ValidationError(
                        propertyName = "sensorWidth",
                        errorMessage = "Sensor width cannot exceed $MAX_SENSOR_DIMENSION mm",
                        attemptedValue = request.sensorWidth
                    )
                )
            }
        }

        // Validate SensorHeight
        if (request.sensorHeight <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "sensorHeight",
                    errorMessage = "Sensor height must be greater than 0",
                    attemptedValue = request.sensorHeight
                )
            )
        } else {
            if (request.sensorHeight < MIN_SENSOR_DIMENSION) {
                errors.add(
                    ValidationError(
                        propertyName = "sensorHeight",
                        errorMessage = "Sensor height must be at least $MIN_SENSOR_DIMENSION mm",
                        attemptedValue = request.sensorHeight
                    )
                )
            }

            if (request.sensorHeight > MAX_SENSOR_DIMENSION) {
                errors.add(
                    ValidationError(
                        propertyName = "sensorHeight",
                        errorMessage = "Sensor height cannot exceed $MAX_SENSOR_DIMENSION mm",
                        attemptedValue = request.sensorHeight
                    )
                )
            }
        }

        // Validate MountType - enum validation
        if (!isValidMountType(request.mountType)) {
            errors.add(
                ValidationError(
                    propertyName = "mountType",
                    errorMessage = "Invalid mount type selected",
                    attemptedValue = request.mountType
                )
            )
        }

        // Cross-field validation: sensor aspect ratio
        if (request.sensorWidth > 0 && request.sensorHeight > 0) {
            val aspectRatio = request.sensorWidth / request.sensorHeight
            if (aspectRatio < 0.5 || aspectRatio > 4.0) {
                errors.add(
                    ValidationError(
                        propertyName = "sensorDimensions",
                        errorMessage = "Sensor aspect ratio is unrealistic (width/height should be between 0.5 and 4.0)",
                        attemptedValue = aspectRatio
                    )
                )
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }

    private fun containsInvalidCharacters(text: String): Boolean {
        // Characters that are problematic in camera names
        val invalidChars = setOf('<', '>', '"', '|', '?', '*', '/', '\\', '\u0000')
        return text.any { it in invalidChars }
    }

    private fun isValidMountType(mountType: MountType): Boolean {
        // Validate that the enum value is one of the defined values
        return try {
            MountType.values().contains(mountType)
        } catch (ex: Exception) {
            false
        }
    }
}