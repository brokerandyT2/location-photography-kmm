package com.x3squaredcircles.photography.application.queries.lens.validators

import com.x3squaredcircles.photography.application.queries.lens.GetLensesQuery
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult
import com.x3squaredcircles.photography.application.common.behaviors.ValidationError

class GetLensesQueryValidator : IValidator<GetLensesQuery> {

    override suspend fun validate(request: GetLensesQuery): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (request.skip < 0) {
            errors.add(
                ValidationError(
                    propertyName = "skip",
                    errorMessage = "Skip must be greater than or equal to 0",
                    attemptedValue = request.skip
                )
            )
        }

        if (request.take <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "take",
                    errorMessage = "Take must be greater than 0",
                    attemptedValue = request.take
                )
            )
        }

        if (request.take > 100) {
            errors.add(
                ValidationError(
                    propertyName = "take",
                    errorMessage = "Take must be less than or equal to 100",
                    attemptedValue = request.take
                )
            )
        }

        if (request.compatibleWithCameraId != null && request.compatibleWithCameraId!! <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "compatibleWithCameraId",
                    errorMessage = "Compatible camera ID must be greater than 0",
                    attemptedValue = request.compatibleWithCameraId!!
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