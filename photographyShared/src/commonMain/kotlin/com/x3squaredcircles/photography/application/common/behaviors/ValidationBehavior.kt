// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/common/behaviors/ValidationBehavior.kt
package com.x3squaredcircles.photography.application.common.behaviors

import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

/**
 * Interface for pipeline behaviors that can intercept and process requests
 */
interface IPipelineBehavior<TRequest, TResponse> {
    suspend fun handle(request: TRequest, next: suspend () -> TResponse): TResponse
}

/**
 * Interface for validators that can validate requests
 */
interface IValidator<TRequest> {
    suspend fun validate(request: TRequest): ValidationResult
}

class ValidationBehavior<TRequest, TResponse>(
    private val validators: List<IValidator<TRequest>>,
    private val logger: Logger
) : IPipelineBehavior<TRequest, TResponse>
        where TRequest : Any,
              TResponse : Any {

    override suspend fun handle(
        request: TRequest,
        next: suspend () -> TResponse
    ): TResponse {
        val requestName = request::class.simpleName ?: "Unknown"

        // Skip validation for queries without validators
        if (validators.isEmpty()) {
            logger.v { "No validators found for $requestName" }
            return next()
        }

        logger.d { "Validating request: $requestName" }

        // Collect all validation failures
        val validationFailures = mutableListOf<ValidationError>()

        for (validator in validators) {
            try {
                val validationResult = validator.validate(request)
                if (!validationResult.isValid) {
                    validationFailures.addAll(validationResult.errors)
                }
            } catch (ex: Exception) {
                logger.e(ex) { "Error during validation for $requestName" }
                validationFailures.add(
                    ValidationError(
                        propertyName = "ValidationSystem",
                        errorMessage = "Validation system error: ${ex.message}",
                        attemptedValue = null
                    )
                )
            }
        }

        // If we have validation failures, handle them based on request type
        if (validationFailures.isNotEmpty()) {
            logger.w { "Validation failed for $requestName with ${validationFailures.size} errors" }

            // Log detailed validation errors
            validationFailures.forEach { error ->
                logger.w { "Validation error - ${error.propertyName}: ${error.errorMessage}" }
            }

            return handleValidationFailures(request, validationFailures, requestName)
        }

        logger.d { "Validation passed for $requestName" }
        return next()
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleValidationFailures(
        request: TRequest,
        failures: List<ValidationError>,
        requestName: String
    ): TResponse {
        val errorMessage = buildValidationErrorMessage(failures)

        // For commands and queries that return Result<T>, return a failure result
        // We'll try to create a Result.failure and cast it, or throw if that fails
        return try {
            // Attempt to create a Result.failure - this will work for Result<T> types
            Result.failure<Any>(errorMessage) as TResponse
        } catch (ex: ClassCastException) {
            // If casting fails, the response type is not Result<T>, so throw an exception
            throw ValidationException(
                message = "Validation failed for $requestName: $errorMessage",
                errors = failures
            )
        }
    }

    private fun buildValidationErrorMessage(failures: List<ValidationError>): String {
        return when (failures.size) {
            1 -> failures.first().errorMessage
            else -> {
                val errorMessages = failures.map { "${it.propertyName}: ${it.errorMessage}" }
                "Multiple validation errors: ${errorMessages.joinToString("; ")}"
            }
        }
    }
}

/**
 * Represents a validation error for a specific property
 */
data class ValidationError(
    val propertyName: String,
    val errorMessage: String,
    val attemptedValue: Any?
)

/**
 * Represents the result of a validation operation
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError> = emptyList()
) {
    companion object {
        fun success(): ValidationResult = ValidationResult(true)

        fun failure(errors: List<ValidationError>): ValidationResult =
            ValidationResult(false, errors)

        fun failure(propertyName: String, errorMessage: String, attemptedValue: Any? = null): ValidationResult =
            ValidationResult(false, listOf(ValidationError(propertyName, errorMessage, attemptedValue)))
    }
}

/**
 * Exception thrown when validation fails and cannot be handled gracefully
 */
class ValidationException(
    message: String,
    val errors: List<ValidationError> = emptyList()
) : Exception(message)