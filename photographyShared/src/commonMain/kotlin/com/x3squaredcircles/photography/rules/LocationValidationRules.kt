// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/rules/LocationValidationRules.kt
package com.x3squaredcircles.photographyshared.rules

import com.x3squaredcircles.core.domain.entities.Location

/**
 * Business rules for location validation
 */
object LocationValidationRules {

    /**
     * Validates the specified Location object and returns a value indicating whether it is valid.
     */
    fun isValid(location: Location): Pair<Boolean, List<String>> {
        val errors = mutableListOf<String>()

        if (location.title.length > 100) {
            errors.add("Location title cannot exceed 100 characters")
        }

        if (location.description.length > 500) {
            errors.add("Location description cannot exceed 500 characters")
        }

        location.photoPath?.let { photoPath ->
            if (photoPath.isNotBlank() && !isValidPath(photoPath)) {
                errors.add("Invalid photo path")
            }
        }

        return Pair(errors.isEmpty(), errors)
    }

    /**
     * Determines whether the specified path is valid by checking for invalid characters.
     */
    private fun isValidPath(path: String): Boolean {
        return try {
            // Basic path validation - check for invalid characters
            val invalidChars = charArrayOf('<', '>', ':', '"', '|', '?', '*', '\u0000')
            !path.any { char -> char in invalidChars }
        } catch (e: Exception) {
            false
        }
    }
}