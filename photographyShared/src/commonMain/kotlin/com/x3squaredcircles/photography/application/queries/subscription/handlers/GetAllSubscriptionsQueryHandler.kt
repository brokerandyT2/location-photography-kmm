// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetAllSubscriptionsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetAllSubscriptionsQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetAllSubscriptionsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllSubscriptionsQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetAllSubscriptionsQuery, GetAllSubscriptionsQueryResult> {

    override suspend fun handle(query: GetAllSubscriptionsQuery): Result<GetAllSubscriptionsQueryResult> {
        logger.d { "Handling GetAllSubscriptionsQuery" }

        return when (val result = subscriptionRepository.getAllAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} subscriptions" }
                Result.success(
                    GetAllSubscriptionsQueryResult(
                        subscriptions = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all subscriptions: ${result.error}" }
                Result.success(
                    GetAllSubscriptionsQueryResult(
                        subscriptions = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}