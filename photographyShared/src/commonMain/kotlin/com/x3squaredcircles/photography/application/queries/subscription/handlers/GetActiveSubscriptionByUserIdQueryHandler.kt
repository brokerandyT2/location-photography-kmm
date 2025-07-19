// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetActiveSubscriptionByUserIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetActiveSubscriptionByUserIdQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetActiveSubscriptionByUserIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetActiveSubscriptionByUserIdQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetActiveSubscriptionByUserIdQuery, GetActiveSubscriptionByUserIdQueryResult> {

    override suspend fun handle(query: GetActiveSubscriptionByUserIdQuery): Result<GetActiveSubscriptionByUserIdQueryResult> {
        logger.d { "Handling GetActiveSubscriptionByUserIdQuery with userId: ${query.userId}" }

        return when (val result = subscriptionRepository.getActiveByUserIdAsync(query.userId, query.currentTime)) {
            is Result.Success -> {
                logger.i { "Retrieved active subscription for userId: ${query.userId}, found: ${result.data != null}" }
                Result.success(
                    GetActiveSubscriptionByUserIdQueryResult(
                        subscription = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get active subscription by userId: ${query.userId} - ${result.error}" }
                Result.success(
                    GetActiveSubscriptionByUserIdQueryResult(
                        subscription = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}