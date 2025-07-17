// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/weather/UpdateWeatherCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.weather

/**
 * Provides validation rules for the UpdateWeatherCommand
 */
class UpdateWeatherCommandValidator {
    /**
     * Validates the UpdateWeatherCommand
     */
    fun validate(command: UpdateWeatherCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (command.locationId <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "locationId",
                    message = "Location ID must be greater than 0"
                )
            )
        }

        return ValidationResult(errors)
    }
}

/**
 * Represents a validation error
 */
data class ValidationError(
    val propertyName: String,
    val message: String
)

/**
 * Represents the result of validation
 */
data class ValidationResult(
    val errors: List<ValidationError>
) {
    val isValid: Boolean
        get() = errors.isEmpty()
}