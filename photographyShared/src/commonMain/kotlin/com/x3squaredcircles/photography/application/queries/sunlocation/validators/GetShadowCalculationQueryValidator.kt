package com.x3squaredcircles.photography.application.queries.sunlocation.validators

import com.x3squaredcircles.photography.application.queries.sunlocation.GetShadowCalculationQuery
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult
import com.x3squaredcircles.photography.application.common.behaviors.ValidationError
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetShadowCalculationQueryValidator : IValidator<GetShadowCalculationQuery> {

    companion object {
        private val MIN_DATE = LocalDate(1900, 1, 1)
        private val MAX_DATE = LocalDate(2100, 12, 31)
    }

    override suspend fun validate(request: GetShadowCalculationQuery): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (request.latitude < -90.0 || request.latitude > 90.0) {
            errors.add(
                ValidationError(
                    propertyName = "latitude",
                    errorMessage = "Latitude must be between -90 and 90 degrees",
                    attemptedValue = request.latitude
                )
            )
        }

        if (request.longitude < -180.0 || request.longitude > 180.0) {
            errors.add(
                ValidationError(
                    propertyName = "longitude",
                    errorMessage = "Longitude must be between -180 and 180 degrees",
                    attemptedValue = request.longitude
                )
            )
        }

        if (!isValidDateTime(request.dateTime)) {
            errors.add(
                ValidationError(
                    propertyName = "dateTime",
                    errorMessage = "DateTime must be a valid date and time within reasonable range",
                    attemptedValue = request.dateTime
                )
            )
        }

        if (request.objectHeight <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "objectHeight",
                    errorMessage = "Object height must be greater than 0",
                    attemptedValue = request.objectHeight
                )
            )
        }

        if (request.objectHeight > 1000) {
            errors.add(
                ValidationError(
                    propertyName = "objectHeight",
                    errorMessage = "Object height must be less than or equal to 1000 meters",
                    attemptedValue = request.objectHeight
                )
            )
        }

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }

    private fun isValidDateTime(dateTime: Instant): Boolean {
        return try {
            val localDateTime = dateTime.toLocalDateTime(TimeZone.UTC)
            val localDate = localDateTime.date

            localDate >= MIN_DATE && localDate <= MAX_DATE
        } catch (e: Exception) {
            false
        }
    }
}