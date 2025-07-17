// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/SaveLocationCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.location

/**
 * Validates the properties of a SaveLocationCommand to ensure they meet the required constraints
 */
class SaveLocationCommandValidator {

    /**
     * Validates the SaveLocationCommand
     */
    fun validate(command: SaveLocationCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Title validation
        if (command.title.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "title",
                    message = "Title is required"
                )
            )
        } else if (command.title.length > 100) {
            errors.add(
                ValidationError(
                    propertyName = "title",
                    message = "Title must not exceed 100 characters"
                )
            )
        }

        // Description validation
        if (command.description.length > 500) {
            errors.add(
                ValidationError(
                    propertyName = "description",
                    message = "Description must not exceed 500 characters"
                )
            )
        }

        // Latitude validation
        if (command.latitude < -90 || command.latitude > 90) {
            errors.add(
                ValidationError(
                    propertyName = "latitude",
                    message = "Latitude must be between -90 and 90 degrees"
                )
            )
        }

        // Longitude validation
        if (command.longitude < -180 || command.longitude > 180) {
            errors.add(
                ValidationError(
                    propertyName = "longitude",
                    message = "Longitude must be between -180 and 180 degrees"
                )
            )
        }

        // Null Island validation (0,0 coordinates)
        if (command.latitude == 0.0 && command.longitude == 0.0) {
            errors.add(
                ValidationError(
                    propertyName = "coordinates",
                    message = "Invalid coordinates: Cannot use Null Island (0,0)"
                )
            )
        }

        // PhotoPath validation (if provided)
        command.photoPath?.let { photoPath ->
            if (photoPath.isNotBlank() && !isValidPath(photoPath)) {
                errors.add(
                    ValidationError(
                        propertyName = "photoPath",
                        message = "Photo path is not valid"
                    )
                )
            }
        }

        return ValidationResult(errors)
    }

    /**
     * Determines whether the specified path is valid
     */
    private fun isValidPath(path: String): Boolean {
        if (path.isBlank()) return true

        // Check for invalid characters
        val invalidChars = listOf('|', '<', '>', '"', '?', '*', '\u0000')
        if (path.any { it in invalidChars }) {
            return false
        }

        // Basic path structure validation
        return try {
            path.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Command for saving a location
 */
data class SaveLocationCommand(
    val id: Int? = null,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val photoPath: String? = null
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