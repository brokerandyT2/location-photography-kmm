// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetSubscriptionByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByIdQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import co.touchlab.kermit.Logger

class GetSubscriptionByIdQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetSubscriptionByIdQuery, GetSubscriptionByIdQueryResult> {

    override suspend fun handle(query: GetSubscriptionByIdQuery): GetSubscriptionByIdQueryResult {
        return try {
            logger.d { "Handling GetSubscriptionByIdQuery with id: ${query.id}" }

            val subscription = subscriptionRepository.getByIdAsync(query.id)

            logger.i { "Retrieved subscription with id: ${query.id}, found: ${subscription != null}" }

            GetSubscriptionByIdQueryResult(
                subscription = subscription,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get subscription by id: ${query.id}" }
            GetSubscriptionByIdQueryResult(
                subscription = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}