// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/DeleteTipCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.tip

/**
 * Provides validation logic for the DeleteTipCommand
 */
class DeleteTipCommandValidator {

    /**
     * Validates the DeleteTipCommand
     */
    fun validate(command: DeleteTipCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (command.id <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "id",
                    message = "Id must be greater than 0"
                )
            )
        }

        return ValidationResult(errors)
    }
}
