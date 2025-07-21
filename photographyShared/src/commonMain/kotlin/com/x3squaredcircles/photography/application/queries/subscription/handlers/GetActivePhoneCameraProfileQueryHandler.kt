// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetSubscriptionStatusQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionStatusQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionStatusQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.domain.services.ISubscriptionService
import com.x3squaredcircles.photography.domain.models.SubscriptionStatusDto
import com.x3squaredcircles.photography.domain.models.SubscriptionStatus
import com.x3squaredcircles.photography.domain.models.SubscriptionPeriod
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

    private fun createDefaultSubscriptionStatus(): SubscriptionStatusDto {
        return SubscriptionStatusDto(
            hasActiveSubscription = false,
            productId = "",
            status = SubscriptionStatus.NONE,
            expirationDate = null,
            purchaseDate = null,
            period = SubscriptionPeriod.NONE,
            isExpiringSoon = false,
            daysUntilExpiration = 0
        )
    }
}