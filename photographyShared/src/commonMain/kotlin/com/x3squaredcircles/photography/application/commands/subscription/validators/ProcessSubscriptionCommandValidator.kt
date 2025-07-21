// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/validators/ProcessSubscriptionCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.subscription.validators

import com.x3squaredcircles.photography.application.commands.subscription.ProcessSubscriptionCommand
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult
import com.x3squaredcircles.photography.application.common.behaviors.ValidationError
import com.x3squaredcircles.photography.domain.models.SubscriptionPeriod

class ProcessSubscriptionCommandValidator : IValidator<ProcessSubscriptionCommand> {

    companion object {
        private val VALID_PRODUCT_PREFIXES = setOf(
            "monthly", "yearly", "premium", "pro", "subscription"
        )
    }

    override suspend fun validate(request: ProcessSubscriptionCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Validate ProductId is not empty
        if (request.productId.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "productId",
                    errorMessage = "Product ID is required",
                    attemptedValue = request.productId
                )
            )
        } else {
            // Validate ProductId format
            if (!isValidProductId(request.productId)) {
                errors.add(
                    ValidationError(
                        propertyName = "productId",
                        errorMessage = "Invalid product ID format. Product ID must contain a valid subscription identifier",
                        attemptedValue = request.productId
                    )
                )
            }
        }

        // Validate Period is a valid enum value
        if (request.period !in SubscriptionPeriod.values()) {
            errors.add(
                ValidationError(
                    propertyName = "period",
                    errorMessage = "Subscription period must be Monthly or Yearly",
                    attemptedValue = request.period.toString()
                )
            )
        }

        // Validate Period is not NONE
        if (request.period == SubscriptionPeriod.NONE) {
            errors.add(
                ValidationError(
                    propertyName = "period",
                    errorMessage = "Subscription period must be Monthly or Yearly",
                    attemptedValue = request.period.toString()
                )
            )
        }

        return if (errors.isEmpty()) {
            ValidationResult.success()
        } else {
            ValidationResult.failure(errors)
        }
    }

    private fun isValidProductId(productId: String): Boolean {
        if (productId.isBlank()) return false

        val lowerProductId = productId.lowercase()

        return VALID_PRODUCT_PREFIXES.any { prefix ->
            lowerProductId.contains(prefix)
        }
    }
}