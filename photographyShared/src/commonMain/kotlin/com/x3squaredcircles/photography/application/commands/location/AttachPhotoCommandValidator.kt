// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/AttachPhotoCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.location

/**
 * Validates the AttachPhotoCommand to ensure that all required properties meet the specified rules
 */
class AttachPhotoCommandValidator {

    /**
     * Validates the AttachPhotoCommand
     */
    fun validate(command: AttachPhotoCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // LocationId validation
        if (command.locationId <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "locationId",
                    message = "LocationId must be greater than 0"
                )
            )
        }

        // PhotoPath validation
        if (command.photoPath.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "photoPath",
                    message = "Photo path is required"
                )
            )
        } else if (!isValidPath(command.photoPath)) {
            errors.add(
                ValidationError(
                    propertyName = "photoPath",
                    message = "Photo path is not valid"
                )
            )
        }

        return ValidationResult(errors)
    }

    /**
     * Determines whether the specified path is valid
     */
    private fun isValidPath(path: String): Boolean {
        if (path.isBlank()) return false

        // Check for invalid characters
        val invalidChars = listOf('|', '<', '>', '"', '?', '*', '\u0000')
        if (path.any { it in invalidChars }) {
            return false
        }

        return true
    }
}

/**
 * Command for attaching a photo to a location
 */
data class AttachPhotoCommand(
    val locationId: Int,
    val photoPath: String
)
