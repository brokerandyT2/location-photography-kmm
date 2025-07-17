// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/GetTipTypeByIdQueryValidator.kt
package com.x3squaredcircles.photography.application.queries.tiptype

/**
 * Validates the GetTipTypeByIdQuery to ensure it meets the required criteria
 */
class GetTipTypeByIdQueryValidator {

    /**
     * Validates the GetTipTypeByIdQuery
     */
    fun validate(query: GetTipTypeByIdQuery): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (query.id <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "id",
                    message = "Id must be greater than 0"
                )
            )
        }

        return ValidationResult(errors)
    }
}

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