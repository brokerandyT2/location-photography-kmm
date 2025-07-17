// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/GetRandomTipCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.tip

/**
 * Validates the properties of a GetRandomTipCommand instance
 */
class GetRandomTipCommandValidator {

    /**
     * Validates the GetRandomTipCommand
     */
    fun validate(command: GetRandomTipCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (command.tipTypeId <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "tipTypeId",
                    message = "TipTypeId must be greater than 0"
                )
            )
        }

        return ValidationResult(errors)
    }
}

/**
 * Command for getting a random tip
 */
data class GetRandomTipCommand(
    val tipTypeId: Int
)

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