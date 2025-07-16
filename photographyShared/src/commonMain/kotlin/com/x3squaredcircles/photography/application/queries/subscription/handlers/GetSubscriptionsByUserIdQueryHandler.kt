// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetSubscriptionsByUserIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import com.x3squaredcircles.photography.application.queries.subscription.SubscriptionDto
import co.touchlab.kermit.Logger

// Query and Result classes
data class GetSubscriptionsByUserIdQuery(
    val userId: String
)

data class GetSubscriptionsByUserIdQueryResult(
    val subscriptions: List<SubscriptionDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

class GetSubscriptionsByUserIdQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetSubscriptionsByUserIdQuery, GetSubscriptionsByUserIdQueryResult> {

    override suspend fun handle(query: GetSubscriptionsByUserIdQuery): GetSubscriptionsByUserIdQueryResult {
        return try {
            logger.d { "Handling GetSubscriptionsByUserIdQuery with userId: ${query.userId}" }

            val subscriptions = subscriptionRepository.getByUserIdAsync(query.userId)

            logger.i { "Retrieved ${subscriptions.size} subscriptions for userId: ${query.userId}" }

            GetSubscriptionsByUserIdQueryResult(
                subscriptions = subscriptions,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get subscriptions by userId: ${query.userId}" }
            GetSubscriptionsByUserIdQueryResult(
                subscriptions = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}