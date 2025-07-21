// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/lens/validators/CreateLensCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.lens.validators

import com.x3squaredcircles.photography.application.commands.lens.CreateLensCommand
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult
import com.x3squaredcircles.photography.application.common.behaviors.ValidationError

class CreateLensCommandValidator : IValidator<CreateLensCommand> {

    companion object {
        private const val MIN_FOCAL_LENGTH = 1.0 // mm
        private const val MAX_FOCAL_LENGTH = 10000.0 // mm (theoretical max for photography)
        private const val MIN_F_STOP = 0.5 // f/0.5 (theoretical fastest)
        private const val MAX_F_STOP = 128.0 // f/128 (practical maximum)
        private const val MAX_LENS_NAME_LENGTH = 100
        private const val MAX_COMPATIBLE_CAMERAS = 1000 // Reasonable limit
    }

    override suspend fun validate(request: CreateLensCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Validate MinMM (minimum focal length)
        if (request.minMM <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "minMM",
                    errorMessage = "Focal length must be greater than 0",
                    attemptedValue = request.minMM
                )
            )
        } else {
            if (request.minMM < MIN_FOCAL_LENGTH) {
                errors.add(
                    ValidationError(
                        propertyName = "minMM",
                        errorMessage = "Minimum focal length must be at least ${MIN_FOCAL_LENGTH}mm",
                        attemptedValue = request.minMM
                    )
                )
            }

            if (request.minMM > MAX_FOCAL_LENGTH) {
                errors.add(
                    ValidationError(
                        propertyName = "minMM",
                        errorMessage = "Minimum focal length cannot exceed ${MAX_FOCAL_LENGTH}mm",
                        attemptedValue = request.minMM
                    )
                )
            }
        }

        // Validate MaxMM (maximum focal length) if provided
        request.maxMM?.let { maxMM ->
            if (maxMM <= 0) {
                errors.add(
                    ValidationError(
                        propertyName = "maxMM",
                        errorMessage = "Maximum focal length must be greater than 0",
                        attemptedValue = maxMM
                    )
                )
            } else {
                if (maxMM < request.minMM) {
                    errors.add(
                        ValidationError(
                            propertyName = "maxMM",
                            errorMessage = "Maximum focal length must be greater than or equal to minimum focal length",
                            attemptedValue = maxMM
                        )
                    )
                }

                if (maxMM > MAX_FOCAL_LENGTH) {
                    errors.add(
                        ValidationError(
                            propertyName = "maxMM",
                            errorMessage = "Maximum focal length cannot exceed ${MAX_FOCAL_LENGTH}mm",
                            attemptedValue = maxMM
                        )
                    )
                }else{}
            }
        }

        // Validate MinFStop (maximum aperture) if provided
        request.minFStop?.let { minFStop ->
            if (minFStop <= 0) {
                errors.add(
                    ValidationError(
                        propertyName = "minFStop",
                        errorMessage = "Minimum f-stop must be greater than 0",
                        attemptedValue = minFStop
                    )
                )
            } else {
                if (minFStop < MIN_F_STOP) {
                    errors.add(
                        ValidationError(
                            propertyName = "minFStop",
                            errorMessage = "Minimum f-stop must be at least f/${MIN_F_STOP}",
                            attemptedValue = minFStop
                        )
                    )
                }

                if (minFStop > MAX_F_STOP) {
                    errors.add(
                        ValidationError(
                            propertyName = "minFStop",
                            errorMessage = "Minimum f-stop cannot exceed f/${MAX_F_STOP}",
                            attemptedValue = minFStop
                        )
                    )
                }else{}
            }
        }

        // Validate MaxFStop (minimum aperture) if provided
        request.maxFStop?.let { maxFStop ->
            if (maxFStop <= 0) {
                errors.add(
                    ValidationError(
                        propertyName = "maxFStop",
                        errorMessage = "Maximum f-stop must be greater than 0",
                        attemptedValue = maxFStop
                    )
                )
            } else {
                // Check against minFStop if both are provided
                request.minFStop?.let { minFStop ->
                    if (maxFStop < minFStop) {
                        errors.add(
                            ValidationError(
                                propertyName = "maxFStop",
                                errorMessage = "Maximum f-stop must be greater than or equal to minimum f-stop",
                                attemptedValue = maxFStop
                            )
                        )
                    }
                }

                if (maxFStop > MAX_F_STOP) {
                    errors.add(
                        ValidationError(
                            propertyName = "maxFStop",
                            errorMessage = "Maximum f-stop cannot exceed f/${MAX_F_STOP}",
                            attemptedValue = maxFStop
                        )
                    )
                }else{}
            }
        }

        // Validate LensName
        if (request.lensName.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "lensName",
                    errorMessage = "Lens name is required",
                    attemptedValue = request.lensName
                )
            )
        } else {
            if (request.lensName.length > MAX_LENS_NAME_LENGTH) {
                errors.add(
                    ValidationError(
                        propertyName = "lensName",
                        errorMessage = "Lens name cannot exceed $MAX_LENS_NAME_LENGTH characters",
                        attemptedValue = request.lensName
                    )
                )
            }

            if (containsInvalidCharacters(request.lensName)) {
                errors.add(
                    ValidationError(
                        propertyName = "lensName",
                        errorMessage = "Lens name contains invalid characters",
                        attemptedValue = request.lensName
                    )
                )
            }
        }

        // Validate CompatibleCameraIds
        if (request.compatibleCameraIds.isEmpty()) {
            errors.add(
                ValidationError(
                    propertyName = "compatibleCameraIds",
                    errorMessage = "At least one compatible camera must be selected",
                    attemptedValue = request.compatibleCameraIds
                )
            )
        } else {
            if (request.compatibleCameraIds.size > MAX_COMPATIBLE_CAMERAS) {
                errors.add(
                    ValidationError(
                        propertyName = "compatibleCameraIds",
                        errorMessage = "Cannot exceed $MAX_COMPATIBLE_CAMERAS compatible cameras",
                        attemptedValue = request.compatibleCameraIds.size
                    )
                )
            }

            // Check for invalid camera IDs (must be positive)
            val invalidIds = request.compatibleCameraIds.filter { it <= 0 }
            if (invalidIds.isNotEmpty()) {
                errors.add(
                    ValidationError(
                        propertyName = "compatibleCameraIds",
                        errorMessage = "All camera IDs must be positive integers",
                        attemptedValue = invalidIds
                    )
                )
            }

            // Check for duplicate camera IDs
            val duplicateIds = request.compatibleCameraIds.groupBy { it }
                .filter { it.value.size > 1 }
                .keys
            if (duplicateIds.isNotEmpty()) {
                errors.add(
                    ValidationError(
                        propertyName = "compatibleCameraIds",
                        errorMessage = "Duplicate camera IDs are not allowed",
                        attemptedValue = duplicateIds
                    )
                )
            }
        }

        // Cross-field validation: lens type consistency
        if (request.maxMM != null && request.maxMM == request.minMM) {
            // Prime lens - focal lengths are equal
            if (request.minFStop != null && request.maxFStop != null &&
                request.minFStop != request.maxFStop) {
                // For prime lenses, min and max f-stop should typically be the same
                // This is more of a warning, but we'll allow it
            }
        }

        // Validate focal length range reasonableness
        request.maxMM?.let { maxMM ->
            val focalRange = maxMM / request.minMM
            if (focalRange > 50.0) { // 50x zoom is extreme but possible
                errors.add(
                    ValidationError(
                        propertyName = "focalRange",
                        errorMessage = "Focal length range appears unrealistic (${focalRange.toInt()}x zoom)",
                        attemptedValue = focalRange
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

    private fun containsInvalidCharacters(text: String): Boolean {
        // Characters that are problematic in lens names
        val invalidChars = setOf('<', '>', '"', '|', '?', '*', '/', '\\', '\u0000')
        return text.any { it in invalidChars }
    }
}