// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tiptype/CreateTipTypeCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.tiptype

/**
 * Provides validation rules for the CreateTipTypeCommand
 */
class CreateTipTypeCommandValidator {

    /**
     * Validates the CreateTipTypeCommand
     */
    fun validate(command: CreateTipTypeCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Name validation
        if (command.name.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "name",
                    message = "Name is required"
                )
            )
        } else if (command.name.length > 100) {
            errors.add(
                ValidationError(
                    propertyName = "name",
                    message = "Name must not exceed 100 characters"
                )
            )
        }

        // I8n (localization) validation
        if (command.i8n.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "i8n",
                    message = "Localization is required"
                )
            )
        } else if (command.i8n.length > 10) {
            errors.add(
                ValidationError(
                    propertyName = "i8n",
                    message = "Localization must not exceed 10 characters"
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