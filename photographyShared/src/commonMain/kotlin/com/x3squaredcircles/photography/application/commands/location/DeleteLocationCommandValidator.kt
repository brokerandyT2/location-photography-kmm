// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/DeleteLocationCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.location

/**
 * Provides validation logic for the DeleteLocationCommand
 */
class DeleteLocationCommandValidator {

    /**
     * Validates the DeleteLocationCommand
     */
    fun validate(command: DeleteLocationCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (command.id <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "id",
                    message = "LocationId must be greater than 0"
                )
            )
        }

        return ValidationResult(errors)
    }
}
