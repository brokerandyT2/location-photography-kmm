package com.x3squaredcircles.photography.application.queries.sunlocation.validators

import com.x3squaredcircles.photography.application.queries.sunlocation.GetSunPathDataQuery
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult
import com.x3squaredcircles.photography.application.common.behaviors.ValidationError
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetSunPathDataQueryValidator : IValidator<GetSunPathDataQuery> {

    companion object {
        private val MIN_DATE = LocalDate(1900, 1, 1)
        private val MAX_DATE = LocalDate(2100, 12, 31)
    }

    override suspend fun validate(request: GetSunPathDataQuery): ValidationResult {
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

        if (!isValidDate(request.date)) {
            errors.add(
                ValidationError(
                    propertyName = "date",
                    errorMessage = "Date must be a valid date within reasonable range",
                    attemptedValue = request.date
                )
            )
        }

        if (request.intervalMinutes < 1 || request.intervalMinutes > 60) {
            errors.add(
                ValidationError(
                    propertyName = "intervalMinutes",
                    errorMessage = "Interval minutes must be between 1 and 60",
                    attemptedValue = request.intervalMinutes
                )
            )
        }

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }

    private fun isValidDate(date: Instant): Boolean {
        return try {
            val localDateTime = date.toLocalDateTime(TimeZone.UTC)
            val localDate = localDateTime.date

            localDate >= MIN_DATE && localDate <= MAX_DATE
        } catch (e: Exception) {
            false
        }
    }
}