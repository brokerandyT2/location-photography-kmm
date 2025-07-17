// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/RestoreLocationCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.location

/**
 * Provides validation logic for the RestoreLocationCommand
 */
class RestoreLocationCommandValidator {

    /**
     * Validates the RestoreLocationCommand
     */
    fun validate(command: RestoreLocationCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (command.id <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "locationId",
                    message = "LocationId must be greater than 0"
                )
            )
        }

        return ValidationResult(errors)
    }
}


