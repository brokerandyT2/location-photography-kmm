// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/exposurecalculator/validators/GetExposureValuesQueryValidator.kt
package com.x3squaredcircles.photography.application.queries.exposurecalculator.validators

import com.x3squaredcircles.photography.application.queries.exposurecalculator.GetExposureValuesQuery
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult
import com.x3squaredcircles.photography.application.common.behaviors.ValidationError
import com.x3squaredcircles.photography.domain.models.ExposureIncrements

class GetExposureValuesQueryValidator : IValidator<GetExposureValuesQuery> {

    override suspend fun validate(request: GetExposureValuesQuery): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Validate Increments enum
        try {
            val isValid = ExposureIncrements.values().contains(request.increments)
            if (!isValid) {
                errors.add(
                    ValidationError(
                        propertyName = "increments",
                        errorMessage = "Invalid exposure increment value. Must be Full, Half, or Third stops.",
                        attemptedValue = request.increments
                    )
                )
            }
        } catch (e: Exception) {
            errors.add(
                ValidationError(
                    propertyName = "increments",
                    errorMessage = "Invalid exposure increment value. Must be Full, Half, or Third stops.",
                    attemptedValue = request.increments
                )
            )
        }

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }
}