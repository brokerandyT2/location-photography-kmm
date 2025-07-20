// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/handlers/CheckSubscriptionStatusCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.subscription.handlers

import com.x3squaredcircles.photography.application.commands.subscription.CheckSubscriptionStatusCommand
import com.x3squaredcircles.photography.application.commands.subscription.CheckSubscriptionStatusCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.domain.services.ISubscriptionService
import com.x3squaredcircles.photography.domain.models.SubscriptionStatusDto
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

class CheckSubscriptionStatusCommandHandler(
    private val subscriptionService: ISubscriptionService,
    private val logger: Logger
) : ICommandHandler<CheckSubscriptionStatusCommand, CheckSubscriptionStatusCommandResult> {

    override suspend fun handle(command: CheckSubscriptionStatusCommand): Result<CheckSubscriptionStatusCommandResult> {
        logger.d { "Handling CheckSubscriptionStatusCommand" }

        return try {
            when (val statusResult = subscriptionService.validateAndUpdateSubscriptionAsync()) {
                is Result.Success -> {
                    val hasActiveSubscription = statusResult.data
                    val now = Clock.System.now()

                    // Create a basic status DTO based on validation result
                    val status = SubscriptionStatusDto(
                        hasActiveSubscription = hasActiveSubscription,
                        productId = if (hasActiveSubscription) "premium" else "",
                        expirationDate = if (hasActiveSubscription) now + 30.days else null,
                        purchaseDate = if (hasActiveSubscription) now else null,
                        isExpiringSoon = false,
                        daysUntilExpiration = if (hasActiveSubscription) 30 else 0
                    )

                    logger.i { "Subscription status checked: hasActive=$hasActiveSubscription" }
                    Result.success(
                        CheckSubscriptionStatusCommandResult(
                            status = status,
                            isSuccess = true
                        )
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to check subscription status: ${statusResult.error}" }

                    val defaultStatus = SubscriptionStatusDto(
                        hasActiveSubscription = false,
                        productId = "",
                        expirationDate = null,
                        purchaseDate = null,
                        isExpiringSoon = false,
                        daysUntilExpiration = 0
                    )

                    Result.success(
                        CheckSubscriptionStatusCommandResult(
                            status = defaultStatus,
                            isSuccess = false,
                            errorMessage = statusResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error checking subscription status" }

            val defaultStatus = SubscriptionStatusDto(
                hasActiveSubscription = false,
                productId = "",
                expirationDate = null,
                purchaseDate = null,
                isExpiringSoon = false,
                daysUntilExpiration = 0
            )

            Result.success(
                CheckSubscriptionStatusCommandResult(
                    status = defaultStatus,
                    isSuccess = false,
                    errorMessage = "Error checking subscription status"
                )
            )
        }
    }
}