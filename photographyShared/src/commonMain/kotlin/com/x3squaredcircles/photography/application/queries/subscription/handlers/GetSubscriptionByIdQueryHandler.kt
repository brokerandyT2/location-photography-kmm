// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetSubscriptionByIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByIdQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetSubscriptionByIdQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetSubscriptionByIdQuery, GetSubscriptionByIdQueryResult> {

    override suspend fun handle(query: GetSubscriptionByIdQuery): Result<GetSubscriptionByIdQueryResult> {
        logger.d { "Handling GetSubscriptionByIdQuery with id: ${query.id}" }

        return when (val result = subscriptionRepository.getByIdAsync(query.id)) {
            is Result.Success -> {
                logger.i { "Retrieved subscription with id: ${query.id}, found: ${result.data != null}" }
                Result.success(
                    GetSubscriptionByIdQueryResult(
                        subscription = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get subscription by id: ${query.id} - ${result.error}" }
                Result.success(
                    GetSubscriptionByIdQueryResult(
                        subscription = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}