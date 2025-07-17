// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetHourlyForecastQueryValidator.kt
package com.x3squaredcircles.photography.application.queries.weather

import kotlinx.datetime.Instant

/**
 * Validates the properties of a GetHourlyForecastQuery instance to ensure they meet the required constraints
 */
class GetHourlyForecastQueryValidator {

    /**
     * Validates the GetHourlyForecastQuery
     */
    fun validate(query: GetHourlyForecastQuery): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // LocationId validation
        if (query.locationId <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "locationId",
                    message = "LocationId must be greater than 0"
                )
            )
        }

        // Time range validation - EndTime must be greater than StartTime when both are provided
        if (query.startTime != null && query.endTime != null && query.endTime <= query.startTime) {
            errors.add(
                ValidationError(
                    propertyName = "endTime",
                    message = "EndTime must be greater than StartTime when both are provided"
                )
            )
        }

        return ValidationResult(errors)
    }
}

/**
 * Query for getting hourly forecast
 */
data class GetHourlyForecastQuery(
    val locationId: Int,
    val startTime: Instant? = null,
    val endTime: Instant? = null
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