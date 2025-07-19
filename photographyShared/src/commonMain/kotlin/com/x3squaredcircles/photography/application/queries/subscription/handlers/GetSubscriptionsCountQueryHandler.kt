// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetSubscriptionsCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionsCountQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionsCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetSubscriptionsCountQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetSubscriptionsCountQuery, GetSubscriptionsCountQueryResult> {

    override suspend fun handle(query: GetSubscriptionsCountQuery): Result<GetSubscriptionsCountQueryResult> {
        logger.d { "Handling GetSubscriptionsCountQuery" }

        return when (val result = subscriptionRepository.getTotalCountAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved total subscriptions count: ${result.data}" }
                Result.success(
                    GetSubscriptionsCountQueryResult(
                        count = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get subscriptions count: ${result.error}" }
                Result.success(
                    GetSubscriptionsCountQueryResult(
                        count = 0L,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}