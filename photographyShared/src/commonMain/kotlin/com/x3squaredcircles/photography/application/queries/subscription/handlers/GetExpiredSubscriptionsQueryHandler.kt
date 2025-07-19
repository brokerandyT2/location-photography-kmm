// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetExpiredSubscriptionsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetExpiredSubscriptionsQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetExpiredSubscriptionsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetExpiredSubscriptionsQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetExpiredSubscriptionsQuery, GetExpiredSubscriptionsQueryResult> {

    override suspend fun handle(query: GetExpiredSubscriptionsQuery): Result<GetExpiredSubscriptionsQueryResult> {
        logger.d { "Handling GetExpiredSubscriptionsQuery with currentTime: ${query.currentTime}" }

        return when (val result = subscriptionRepository.getExpiredAsync(query.currentTime)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} expired subscriptions" }
                Result.success(
                    GetExpiredSubscriptionsQueryResult(
                        subscriptions = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get expired subscriptions: ${result.error}" }
                Result.success(
                    GetExpiredSubscriptionsQueryResult(
                        subscriptions = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}