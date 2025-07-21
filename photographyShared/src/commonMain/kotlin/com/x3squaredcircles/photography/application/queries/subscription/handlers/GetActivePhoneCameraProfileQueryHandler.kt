package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionStatusQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionStatusQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.domain.services.ISubscriptionService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetSubscriptionStatusQueryHandler(
    private val subscriptionService: ISubscriptionService,
    private val logger: Logger
) : IQueryHandler<GetSubscriptionStatusQuery, GetSubscriptionStatusQueryResult> {

    override suspend fun handle(query: GetSubscriptionStatusQuery): Result<GetSubscriptionStatusQueryResult> {
        logger.d { "Handling GetSubscriptionStatusQuery" }

        return try {
            when (val result = subscriptionService.getCurrentSubscriptionStatusAsync()) {
                is Result.Success -> {
                    logger.i { "Retrieved current subscription status" }
                    Result.success(
                        GetSubscriptionStatusQueryResult(
                            status = result.data,
                            isSuccess = true
                        )
                    )
                }
                is Result.Failure -> {
                    logger.e { "Failed to get subscription status: ${result.error}" }
                    Result.success(
                        GetSubscriptionStatusQueryResult(
                            status = createDefaultSubscriptionStatus(),
                            isSuccess = false,
                            errorMessage = result.error
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error retrieving subscription status" }
            Result.success(
                GetSubscriptionStatusQueryResult(
                    status = createDefaultSubscriptionStatus(),
                    isSuccess = false,
                    errorMessage = "Subscription status retrieval failed: ${ex.message}"
                )
            )
        }
    }

    private fun createDefaultSubscriptionStatus(): com.x3squaredcircles.photography.domain.models.SubscriptionStatusDto {
        return com.x3squaredcircles.photography.domain.models.SubscriptionStatusDto(
            hasActiveSubscription = false,
            productId = "",
            status = com.x3squaredcircles.photography.domain.enums.SubscriptionStatus.INACTIVE,
            expirationDate = null,
            purchaseDate = null,
            period = com.x3squaredcircles.photography.domain.enums.SubscriptionPeriod.MONTHLY,
            isExpiringSoon = false,
            daysUntilExpiration = 0
        )
    }
}