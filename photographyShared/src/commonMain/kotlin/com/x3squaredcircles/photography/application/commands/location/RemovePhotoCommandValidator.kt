// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/RemovePhotoCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.location

/**
 * Provides validation rules for the RemovePhotoCommand
 */
class RemovePhotoCommandValidator {

    /**
     * Validates the RemovePhotoCommand
     */
    fun validate(command: RemovePhotoCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (command.locationId <= 0) {
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

/**
 * Command for removing a photo from a location
 */
data class RemovePhotoCommand(
    val locationId: Int
)
