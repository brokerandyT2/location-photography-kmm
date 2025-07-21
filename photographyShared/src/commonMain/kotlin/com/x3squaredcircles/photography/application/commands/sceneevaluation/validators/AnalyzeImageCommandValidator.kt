// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/sceneevaluation/validators/AnalyzeImageCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.sceneevaluation.validators

import com.x3squaredcircles.photography.application.commands.sceneevaluation.AnalyzeImageCommand
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult
import com.x3squaredcircles.photography.application.common.behaviors.ValidationError

class AnalyzeImageCommandValidator : IValidator<AnalyzeImageCommand> {

    companion object {
        private val VALID_IMAGE_EXTENSIONS = setOf(
            ".jpg", ".jpeg", ".png", ".bmp", ".gif",
            ".tiff", ".tif", ".webp", ".heic", ".heif"
        )
    }

    override suspend fun validate(request: AnalyzeImageCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Validate ImagePath is not empty
        if (request.imagePath.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "imagePath",
                    errorMessage = "Image path is required",
                    attemptedValue = request.imagePath
                )
            )
        } else {
            // Validate path is valid
            if (!isValidPath(request.imagePath)) {
                errors.add(
                    ValidationError(
                        propertyName = "imagePath",
                        errorMessage = "Invalid image path format",
                        attemptedValue = request.imagePath
                    )
                )
            }

            // Validate file extension
            if (!hasValidImageExtension(request.imagePath)) {
                errors.add(
                    ValidationError(
                        propertyName = "imagePath",
                        errorMessage = "Unsupported image file format. Supported formats: ${VALID_IMAGE_EXTENSIONS.joinToString(", ")}",
                        attemptedValue = request.imagePath
                    )
                )
            }

            // Validate path length
            if (request.imagePath.length > 260) {
                errors.add(
                    ValidationError(
                        propertyName = "imagePath",
                        errorMessage = "Image path exceeds maximum length (260 characters)",
                        attemptedValue = request.imagePath
                    )
                )
            }

            // Validate path doesn't contain invalid characters
            if (containsInvalidCharacters(request.imagePath)) {
                errors.add(
                    ValidationError(
                        propertyName = "imagePath",
                        errorMessage = "Image path contains invalid characters",
                        attemptedValue = request.imagePath
                    )
                )
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }

    private fun isValidPath(path: String): Boolean {
        if (path.isBlank()) return false

        // Basic path validation - ensure it's not just whitespace or special characters
        return path.trim().isNotEmpty() && !path.all { it.isWhitespace() }
    }

    private fun hasValidImageExtension(path: String): Boolean {
        if (path.isBlank()) return false

        val extension = path.substringAfterLast('.', "").lowercase()
        if (extension.isEmpty()) return false

        return VALID_IMAGE_EXTENSIONS.contains(".$extension")
    }

    private fun containsInvalidCharacters(path: String): Boolean {
        // Characters that are typically invalid in file paths
        val invalidChars = setOf('|', '<', '>', '"', '?', '*', '\u0000')
        return path.any { it in invalidChars }
    }
}