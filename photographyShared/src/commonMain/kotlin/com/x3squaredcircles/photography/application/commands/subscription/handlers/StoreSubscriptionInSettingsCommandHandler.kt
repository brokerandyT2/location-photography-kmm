// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/handlers/StoreSubscriptionInSettingsCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.subscription.handlers

import com.x3squaredcircles.photography.application.commands.subscription.StoreSubscriptionInSettingsCommand
import com.x3squaredcircles.photography.application.commands.subscription.StoreSubscriptionInSettingsCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.application.common.interfaces.IUnitOfWork
import com.x3squaredcircles.photography.application.common.constants.SubscriptionConstants
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class StoreSubscriptionInSettingsCommandHandler(
    private val unitOfWork: IUnitOfWork,
    private val logger: Logger
) : ICommandHandler<StoreSubscriptionInSettingsCommand, StoreSubscriptionInSettingsCommandResult> {

    override suspend fun handle(command: StoreSubscriptionInSettingsCommand): Result<StoreSubscriptionInSettingsCommandResult> {
        logger.d { "Handling StoreSubscriptionInSettingsCommand for productId: ${command.productId}" }

        return try {
            // Determine subscription type based on product ID
            val subscriptionType = determineSubscriptionType(command.productId)

            // Store subscription type
            when (val typeResult = unitOfWork.settings.upsertAsync(
                key = SubscriptionConstants.SUBSCRIPTION_TYPE,
                value = subscriptionType,
                description = "Current subscription type"
            )) {
                is Result.Success -> logger.d { "Stored subscription type: $subscriptionType" }
                is Result.Failure -> {
                    logger.e { "Failed to store subscription type: ${typeResult.error}" }
                    return Result.success(
                        StoreSubscriptionInSettingsCommandResult(
                            isSuccess = false,
                            errorMessage = "Failed to store subscription type: ${typeResult.error}"
                        )
                    )
                }
            }

            // Store expiration date
            when (val expirationResult = unitOfWork.settings.upsertAsync(
                key = SubscriptionConstants.SUBSCRIPTION_EXPIRATION,
                value = command.expirationDate.toEpochMilliseconds().toString(),
                description = "Subscription expiration date"
            )) {
                is Result.Success -> logger.d { "Stored expiration date: ${command.expirationDate}" }
                is Result.Failure -> {
                    logger.e { "Failed to store expiration date: ${expirationResult.error}" }
                    return Result.success(
                        StoreSubscriptionInSettingsCommandResult(
                            isSuccess = false,
                            errorMessage = "Failed to store expiration date: ${expirationResult.error}"
                        )
                    )
                }
            }

            // Store product ID
            when (val productResult = unitOfWork.settings.upsertAsync(
                key = SubscriptionConstants.SUBSCRIPTION_PRODUCT_ID,
                value = command.productId,
                description = "Subscription product identifier"
            )) {
                is Result.Success -> logger.d { "Stored product ID: ${command.productId}" }
                is Result.Failure -> {
                    logger.e { "Failed to store product ID: ${productResult.error}" }
                    return Result.success(
                        StoreSubscriptionInSettingsCommandResult(
                            isSuccess = false,
                            errorMessage = "Failed to store product ID: ${productResult.error}"
                        )
                    )
                }
            }

            // Store purchase date
            when (val purchaseResult = unitOfWork.settings.upsertAsync(
                key = SubscriptionConstants.SUBSCRIPTION_PURCHASE_DATE,
                value = command.purchaseDate.toEpochMilliseconds().toString(),
                description = "Subscription purchase date"
            )) {
                is Result.Success -> logger.d { "Stored purchase date: ${command.purchaseDate}" }
                is Result.Failure -> {
                    logger.e { "Failed to store purchase date: ${purchaseResult.error}" }
                    return Result.success(
                        StoreSubscriptionInSettingsCommandResult(
                            isSuccess = false,
                            errorMessage = "Failed to store purchase date: ${purchaseResult.error}"
                        )
                    )
                }
            }

            // Store transaction ID
            when (val transactionResult = unitOfWork.settings.upsertAsync(
                key = SubscriptionConstants.SUBSCRIPTION_TRANSACTION_ID,
                value = command.transactionId,
                description = "Subscription transaction identifier"
            )) {
                is Result.Success -> logger.d { "Stored transaction ID: ${command.transactionId}" }
                is Result.Failure -> {
                    logger.e { "Failed to store transaction ID: ${transactionResult.error}" }
                    return Result.success(
                        StoreSubscriptionInSettingsCommandResult(
                            isSuccess = false,
                            errorMessage = "Failed to store transaction ID: ${transactionResult.error}"
                        )
                    )
                }
            }

            // Save changes
            try {
                unitOfWork.saveChangesAsync()
                logger.i { "Successfully stored subscription settings for productId: ${command.productId}" }

                Result.success(
                    StoreSubscriptionInSettingsCommandResult(
                        isSuccess = true
                    )
                )
            } catch (ex: Exception) {
                logger.e(ex) { "Failed to save subscription settings changes" }
                Result.success(
                    StoreSubscriptionInSettingsCommandResult(
                        isSuccess = false,
                        errorMessage = "Failed to save subscription settings: ${ex.message}"
                    )
                )
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error storing subscription in settings for productId: ${command.productId}" }
            Result.success(
                StoreSubscriptionInSettingsCommandResult(
                    isSuccess = false,
                    errorMessage = "Error storing subscription settings: ${ex.message}"
                )
            )
        }
    }

    private fun determineSubscriptionType(productId: String): String {
        val lowerProductId = productId.lowercase()

        return when {
            lowerProductId.contains("premium") -> SubscriptionConstants.PREMIUM
            lowerProductId.contains("professional") || lowerProductId.contains("pro") -> SubscriptionConstants.PRO
            else -> SubscriptionConstants.PREMIUM // Default fallback
        }
    }
}