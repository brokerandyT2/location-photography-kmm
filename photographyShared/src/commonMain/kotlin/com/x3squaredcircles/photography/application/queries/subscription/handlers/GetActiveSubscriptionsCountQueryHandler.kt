// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetActiveSubscriptionsCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetActiveSubscriptionsCountQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetActiveSubscriptionsCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import co.touchlab.kermit.Logger

class GetActiveSubscriptionsCountQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetActiveSubscriptionsCountQuery, GetActiveSubscriptionsCountQueryResult> {

    override suspend fun handle(query: GetActiveSubscriptionsCountQuery): GetActiveSubscriptionsCountQueryResult {
        return try {
            logger.d { "Handling GetActiveSubscriptionsCountQuery with currentTime: ${query.currentTime}" }

            val count = subscriptionRepository.getActiveCountAsync(query.currentTime)

            logger.i { "Retrieved active subscriptions count: $count" }

            GetActiveSubscriptionsCountQueryResult(
                count = count,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get active subscriptions count" }
            GetActiveSubscriptionsCountQueryResult(
                count = 0L,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}