// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetSubscriptionsCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionsCountQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionsCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import co.touchlab.kermit.Logger

class GetSubscriptionsCountQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetSubscriptionsCountQuery, GetSubscriptionsCountQueryResult> {

    override suspend fun handle(query: GetSubscriptionsCountQuery): GetSubscriptionsCountQueryResult {
        return try {
            logger.d { "Handling GetSubscriptionsCountQuery" }

            val count = subscriptionRepository.getTotalCountAsync()

            logger.i { "Retrieved total subscriptions count: $count" }

            GetSubscriptionsCountQueryResult(
                count = count,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get subscriptions count" }
            GetSubscriptionsCountQueryResult(
                count = 0L,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}