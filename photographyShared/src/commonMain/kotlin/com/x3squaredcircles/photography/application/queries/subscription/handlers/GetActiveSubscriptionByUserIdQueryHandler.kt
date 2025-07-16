// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetActiveSubscriptionByUserIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetActiveSubscriptionByUserIdQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetActiveSubscriptionByUserIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler

import co.touchlab.kermit.Logger
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository

class GetActiveSubscriptionByUserIdQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetActiveSubscriptionByUserIdQuery, GetActiveSubscriptionByUserIdQueryResult> {

    override suspend fun handle(query: GetActiveSubscriptionByUserIdQuery): GetActiveSubscriptionByUserIdQueryResult {
        return try {
            logger.d { "Handling GetActiveSubscriptionByUserIdQuery with userId: ${query.userId}" }

            val subscription = subscriptionRepository.getActiveByUserIdAsync(query.userId, query.currentTime)

            logger.i { "Retrieved active subscription for userId: ${query.userId}, found: ${subscription != null}" }

            GetActiveSubscriptionByUserIdQueryResult(
                subscription = subscription,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get active subscription by userId: ${query.userId}" }
            GetActiveSubscriptionByUserIdQueryResult(
                subscription = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}