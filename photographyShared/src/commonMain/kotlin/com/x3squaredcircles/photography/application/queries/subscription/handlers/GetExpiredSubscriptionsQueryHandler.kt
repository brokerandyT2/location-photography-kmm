// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetExpiredSubscriptionsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetExpiredSubscriptionsQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetExpiredSubscriptionsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import co.touchlab.kermit.Logger

class GetExpiredSubscriptionsQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetExpiredSubscriptionsQuery, GetExpiredSubscriptionsQueryResult> {

    override suspend fun handle(query: GetExpiredSubscriptionsQuery): GetExpiredSubscriptionsQueryResult {
        return try {
            logger.d { "Handling GetExpiredSubscriptionsQuery with currentTime: ${query.currentTime}" }

            val subscriptions = subscriptionRepository.getExpiredAsync(query.currentTime)

            logger.i { "Retrieved ${subscriptions.size} expired subscriptions" }

            GetExpiredSubscriptionsQueryResult(
                subscriptions = subscriptions,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get expired subscriptions" }
            GetExpiredSubscriptionsQueryResult(
                subscriptions = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}