// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetAllSubscriptionsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetAllSubscriptionsQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetAllSubscriptionsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import co.touchlab.kermit.Logger

class GetAllSubscriptionsQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetAllSubscriptionsQuery, GetAllSubscriptionsQueryResult> {

    override suspend fun handle(query: GetAllSubscriptionsQuery): GetAllSubscriptionsQueryResult {
        return try {
            logger.d { "Handling GetAllSubscriptionsQuery" }

            val subscriptions = subscriptionRepository.getAllAsync()

            logger.i { "Retrieved ${subscriptions.size} subscriptions" }

            GetAllSubscriptionsQueryResult(
                subscriptions = subscriptions,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all subscriptions" }
            GetAllSubscriptionsQueryResult(
                subscriptions = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}