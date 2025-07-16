// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetSubscriptionByPurchaseTokenQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByPurchaseTokenQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByPurchaseTokenQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import co.touchlab.kermit.Logger

class GetSubscriptionByPurchaseTokenQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetSubscriptionByPurchaseTokenQuery, GetSubscriptionByPurchaseTokenQueryResult> {

    override suspend fun handle(query: GetSubscriptionByPurchaseTokenQuery): GetSubscriptionByPurchaseTokenQueryResult {
        return try {
            logger.d { "Handling GetSubscriptionByPurchaseTokenQuery with purchaseToken: ${query.purchaseToken}" }

            val subscription = subscriptionRepository.getByPurchaseTokenAsync(query.purchaseToken)

            logger.i { "Retrieved subscription with purchaseToken: ${query.purchaseToken}, found: ${subscription != null}" }

            GetSubscriptionByPurchaseTokenQueryResult(
                subscription = subscription,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get subscription by purchaseToken: ${query.purchaseToken}" }
            GetSubscriptionByPurchaseTokenQueryResult(
                subscription = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}