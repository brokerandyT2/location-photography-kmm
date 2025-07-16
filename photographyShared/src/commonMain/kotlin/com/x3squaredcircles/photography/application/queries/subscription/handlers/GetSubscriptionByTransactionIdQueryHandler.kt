// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetSubscriptionByTransactionIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByTransactionIdQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByTransactionIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import co.touchlab.kermit.Logger

class GetSubscriptionByTransactionIdQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetSubscriptionByTransactionIdQuery, GetSubscriptionByTransactionIdQueryResult> {

    override suspend fun handle(query: GetSubscriptionByTransactionIdQuery): GetSubscriptionByTransactionIdQueryResult {
        return try {
            logger.d { "Handling GetSubscriptionByTransactionIdQuery with transactionId: ${query.transactionId}" }

            val subscription = subscriptionRepository.getByTransactionIdAsync(query.transactionId)

            logger.i { "Retrieved subscription with transactionId: ${query.transactionId}, found: ${subscription != null}" }

            GetSubscriptionByTransactionIdQueryResult(
                subscription = subscription,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get subscription by transactionId: ${query.transactionId}" }
            GetSubscriptionByTransactionIdQueryResult(
                subscription = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}