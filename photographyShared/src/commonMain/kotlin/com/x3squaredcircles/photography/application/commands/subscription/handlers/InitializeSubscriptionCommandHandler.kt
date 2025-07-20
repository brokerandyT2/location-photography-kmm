// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/handlers/InitializeSubscriptionCommandHandler.kt
package com.x3squaredcircles.photography.application.commands.subscription.handlers

import com.x3squaredcircles.photography.application.commands.subscription.InitializeSubscriptionCommand
import com.x3squaredcircles.photography.application.commands.subscription.InitializeSubscriptionCommandResult
import com.x3squaredcircles.photography.application.commands.ICommandHandler
import com.x3squaredcircles.photography.domain.services.ISubscriptionService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class InitializeSubscriptionCommandHandler(
    private val subscriptionService: ISubscriptionService,
    private val logger: Logger
) : ICommandHandler<InitializeSubscriptionCommand, InitializeSubscriptionCommandResult> {

    override suspend fun handle(command: InitializeSubscriptionCommand): Result<InitializeSubscriptionCommandResult> {
        logger.d { "Handling InitializeSubscriptionCommand" }

        return try {
            when (val initResult = subscriptionService.validateAndUpdateSubscriptionAsync()) {
                is Result.Success -> {
                    if (initResult.data) {
                        logger.i { "Subscription service initialized successfully" }
                        Result.success(
                            InitializeSubscriptionCommandResult(
                                products = emptyList(), // Would load from service if available
                                isConnected = true,
                                isSuccess = true
                            )
                        )
                    } else {
                        logger.w { "Subscription service initialization failed" }
                        Result.success(
                            InitializeSubscriptionCommandResult(
                                products = emptyList(),
                                isConnected = false,
                                isSuccess = false,
                                errorMessage = "Subscription service not available"
                            )
                        )
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to initialize subscription service: ${initResult.error}" }
                    Result.success(
                        InitializeSubscriptionCommandResult(
                            products = emptyList(),
                            isConnected = false,
                            isSuccess = false,
                            errorMessage = initResult.error
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error initializing subscription" }
            Result.success(
                InitializeSubscriptionCommandResult(
                    products = emptyList(),
                    isConnected = false,
                    isSuccess = false,
                    errorMessage = "Error initializing subscription"
                )
            )
        }
    }
}