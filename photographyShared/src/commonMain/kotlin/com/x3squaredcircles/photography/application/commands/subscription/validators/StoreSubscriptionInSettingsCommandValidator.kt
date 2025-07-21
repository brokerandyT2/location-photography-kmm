// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/validators/StoreSubscriptionInSettingsCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.subscription.validators

import com.x3squaredcircles.photography.application.commands.subscription.StoreSubscriptionInSettingsCommand
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult
import com.x3squaredcircles.photography.application.common.behaviors.ValidationError
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

class StoreSubscriptionInSettingsCommandValidator : IValidator<StoreSubscriptionInSettingsCommand> {

    companion object {
        private val VALID_PRODUCT_PREFIXES = setOf(
            "monthly", "yearly", "premium", "pro", "subscription"
        )
        private val MIN_DATE = Instant.fromEpochMilliseconds(1577836800000) // 2020-01-01
    }

    override suspend fun validate(request: StoreSubscriptionInSettingsCommand): ValidationResult {
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
                        errorMessage = "Invalid product ID format",
                        attemptedValue = request.productId
                    )
                )
            }
        }

        // Validate ExpirationDate
        if (request.expirationDate == Instant.DISTANT_PAST) {
            errors.add(
                ValidationError(
                    propertyName = "expirationDate",
                    errorMessage = "Expiration date is required",
                    attemptedValue = request.expirationDate.toString()
                )
            )
        } else {
            // Must be in the future
            if (request.expirationDate <= Clock.System.now()) {
                errors.add(
                    ValidationError(
                        propertyName = "expirationDate",
                        errorMessage = "Expiration date must be in the future",
                        attemptedValue = request.expirationDate.toString()
                    )
                )
            }
        }

        // Validate PurchaseDate
        if (request.purchaseDate == Instant.DISTANT_PAST) {
            errors.add(
                ValidationError(
                    propertyName = "purchaseDate",
                    errorMessage = "Purchase date is required",
                    attemptedValue = request.purchaseDate.toString()
                )
            )
        } else {
            val now = Clock.System.now()
            val oneDayFromNow = Clock.System.now().plus(1.days)

            // Purchase date must be reasonable (not too far in past, not in future)
            if (request.purchaseDate < MIN_DATE || request.purchaseDate > oneDayFromNow) {
                errors.add(
                    ValidationError(
                        propertyName = "purchaseDate",
                        errorMessage = "Purchase date must be a valid date not in the future",
                        attemptedValue = request.purchaseDate.toString()
                    )
                )
            }
        }

        // Validate TransactionId
        if (request.transactionId.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "transactionId",
                    errorMessage = "Transaction ID is required",
                    attemptedValue = request.transactionId
                )
            )
        } else if (request.transactionId.length < 5) {
            errors.add(
                ValidationError(
                    propertyName = "transactionId",
                    errorMessage = "Transaction ID must be at least 5 characters long",
                    attemptedValue = request.transactionId
                )
            )
        }

        // Validate date range (purchase before expiration)
        if (request.purchaseDate != Instant.DISTANT_PAST &&
            request.expirationDate != Instant.DISTANT_PAST &&
            request.purchaseDate >= request.expirationDate) {
            errors.add(
                ValidationError(
                    propertyName = "dateRange",
                    errorMessage = "Purchase date must be before expiration date",
                    attemptedValue = "Purchase: ${request.purchaseDate}, Expiration: ${request.expirationDate}"
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