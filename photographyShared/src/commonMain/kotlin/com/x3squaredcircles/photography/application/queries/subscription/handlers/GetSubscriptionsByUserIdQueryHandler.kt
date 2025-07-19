// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetSubscriptionsByUserIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import com.x3squaredcircles.photography.application.queries.subscription.SubscriptionDto
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

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

    override suspend fun handle(query: GetSubscriptionsByUserIdQuery): Result<GetSubscriptionsByUserIdQueryResult> {
        logger.d { "Handling GetSubscriptionsByUserIdQuery with userId: ${query.userId}" }

        return when (val result = subscriptionRepository.getByUserIdAsync(query.userId)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} subscriptions for userId: ${query.userId}" }
                Result.success(
                    GetSubscriptionsByUserIdQueryResult(
                        subscriptions = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get subscriptions by userId: ${query.userId} - ${result.error}" }
                Result.success(
                    GetSubscriptionsByUserIdQueryResult(
                        subscriptions = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}