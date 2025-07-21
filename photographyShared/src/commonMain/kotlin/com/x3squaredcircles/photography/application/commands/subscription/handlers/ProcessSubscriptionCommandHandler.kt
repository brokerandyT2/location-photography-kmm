// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/handlers/ProcessSubscriptionCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.subscription.handlers

import com.x3squaredcircles.photography.application.commands.subscription.ProcessSubscriptionCommand
import com.x3squaredcircles.photography.application.commands.subscription.ProcessSubscriptionCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.domain.services.ISubscriptionService
import com.x3squaredcircles.photography.domain.models.ProcessSubscriptionResultDto
import com.x3squaredcircles.photography.domain.models.SubscriptionStatus
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock

class ProcessSubscriptionCommandHandler(
    private val subscriptionService: ISubscriptionService,
    private val logger: Logger
) : ICommandHandler<ProcessSubscriptionCommand, ProcessSubscriptionCommandResult> {

    override suspend fun handle(command: ProcessSubscriptionCommand): Result<ProcessSubscriptionCommandResult> {
        logger.d { "Handling ProcessSubscriptionCommand for productId: ${command.productId}" }

        return try {
            when (val result = subscriptionService.purchaseSubscriptionAsync(command.productId)) {
                is Result.Success -> {
                    logger.i { "Subscription purchase successful for productId: ${command.productId}" }

                    // Store subscription data
                    when (val storeResult = subscriptionService.storeSubscriptionAsync(result.data)) {
                        is Result.Success -> {
                            logger.i { "Subscription data stored successfully" }
                            Result.success(
                                ProcessSubscriptionCommandResult(
                                    result = result.data,
                                    isSuccess = true
                                )
                            )
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to store subscription: ${storeResult.error}" }
                            Result.success(
                                ProcessSubscriptionCommandResult(
                                    result = createFailedResult(command.productId, storeResult.error),
                                    isSuccess = false,
                                    errorMessage = storeResult.error
                                )
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to purchase subscription: ${result.error}" }
                    Result.success(
                        ProcessSubscriptionCommandResult(
                            result = createFailedResult(command.productId, result.error),
                            isSuccess = false,
                            errorMessage = result.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error processing subscription for productId: ${command.productId}" }
            Result.success(
                ProcessSubscriptionCommandResult(
                    result = createFailedResult(command.productId, "Failed to process subscription"),
                    isSuccess = false,
                    errorMessage = "Failed to process subscription: ${ex.message}"
                )
            )
        }
    }

    private fun createFailedResult(productId: String, errorMessage: String): ProcessSubscriptionResultDto {
        val currentTime = Clock.System.now()
        return ProcessSubscriptionResultDto(
            productId = productId,
            transactionId = "",
            purchaseToken = "",
            purchaseDate = currentTime,
            expirationDate = currentTime,
            status = SubscriptionStatus.FAILED
        )
    }
}