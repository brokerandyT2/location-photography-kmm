// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetSubscriptionByPurchaseTokenQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByPurchaseTokenQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByPurchaseTokenQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetSubscriptionByPurchaseTokenQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetSubscriptionByPurchaseTokenQuery, GetSubscriptionByPurchaseTokenQueryResult> {

    override suspend fun handle(query: GetSubscriptionByPurchaseTokenQuery): Result<GetSubscriptionByPurchaseTokenQueryResult> {
        logger.d { "Handling GetSubscriptionByPurchaseTokenQuery with purchaseToken: ${query.purchaseToken}" }

        return when (val result = subscriptionRepository.getByPurchaseTokenAsync(query.purchaseToken)) {
            is Result.Success -> {
                logger.i { "Retrieved subscription with purchaseToken: ${query.purchaseToken}, found: ${result.data != null}" }
                Result.success(
                    GetSubscriptionByPurchaseTokenQueryResult(
                        subscription = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get subscription by purchaseToken: ${query.purchaseToken} - ${result.error}" }
                Result.success(
                    GetSubscriptionByPurchaseTokenQueryResult(
                        subscription = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}