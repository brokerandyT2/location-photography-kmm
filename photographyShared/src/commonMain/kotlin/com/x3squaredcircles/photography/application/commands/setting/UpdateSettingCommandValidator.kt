// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/UpdateSettingCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.setting

/**
 * Provides validation rules for the UpdateSettingCommand
 */
class UpdateSettingCommandValidator {

    /**
     * Validates the UpdateSettingCommand
     */
    fun validate(command: UpdateSettingCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Key validation
        if (command.key.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "key",
                    message = "Key is required"
                )
            )
        } else if (command.key.length > 50) {
            errors.add(
                ValidationError(
                    propertyName = "key",
                    message = "Key must not exceed 50 characters"
                )
            )
        }

        // Value validation
        if (command.value.length > 500) {
            errors.add(
                ValidationError(
                    propertyName = "value",
                    message = "Value must not exceed 500 characters"
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