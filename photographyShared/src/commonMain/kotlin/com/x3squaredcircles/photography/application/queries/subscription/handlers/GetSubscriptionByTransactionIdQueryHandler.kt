// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/handlers/GetSubscriptionByTransactionIdQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.subscription.handlers

import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByTransactionIdQuery
import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionByTransactionIdQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetSubscriptionByTransactionIdQueryHandler(
    private val subscriptionRepository: ISubscriptionRepository,
    private val logger: Logger
) : IQueryHandler<GetSubscriptionByTransactionIdQuery, GetSubscriptionByTransactionIdQueryResult> {

    override suspend fun handle(query: GetSubscriptionByTransactionIdQuery): Result<GetSubscriptionByTransactionIdQueryResult> {
        logger.d { "Handling GetSubscriptionByTransactionIdQuery with transactionId: ${query.transactionId}" }

        return when (val result = subscriptionRepository.getByTransactionIdAsync(query.transactionId)) {
            is Result.Success -> {
                logger.i { "Retrieved subscription with transactionId: ${query.transactionId}, found: ${result.data != null}" }
                Result.success(
                    GetSubscriptionByTransactionIdQueryResult(
                        subscription = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get subscription by transactionId: ${query.transactionId} - ${result.error}" }
                Result.success(
                    GetSubscriptionByTransactionIdQueryResult(
                        subscription = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}