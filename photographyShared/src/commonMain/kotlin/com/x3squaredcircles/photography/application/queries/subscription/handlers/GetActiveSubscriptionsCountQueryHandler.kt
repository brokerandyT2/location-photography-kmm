// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetActiveSubscriptionsCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetActiveSubscriptionsCountQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetActiveSubscriptionsCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetActiveSubscriptionsCountQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetActiveSubscriptionsCountQuery, GetActiveSubscriptionsCountQueryResult> {

    override suspend fun handle(query: GetActiveSubscriptionsCountQuery): Result<GetActiveSubscriptionsCountQueryResult> {
        logger.d { "Handling GetActiveSubscriptionsCountQuery with currentTime: ${query.currentTime}" }

        return when (val result = subscriptionRepository.getActiveCountAsync(query.currentTime)) {
            is Result.Success -> {
                logger.i { "Retrieved active subscriptions count: ${result.data}" }
                Result.success(
                    GetActiveSubscriptionsCountQueryResult(
                        count = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get active subscriptions count: ${result.error}" }
                Result.success(
                    GetActiveSubscriptionsCountQueryResult(
                        count = 0L,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}