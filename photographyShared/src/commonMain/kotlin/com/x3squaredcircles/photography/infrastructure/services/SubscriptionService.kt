// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/SubscriptionService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.models.ProcessSubscriptionResultDto
import com.x3squaredcircles.photography.domain.models.SubscriptionStatusDto
import com.x3squaredcircles.photography.domain.services.ISubscriptionService
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISubscriptionRepository
import com.x3squaredcircles.photography.application.queries.subscription.SubscriptionDto
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class SubscriptionService(
    private val logger: Logger,
    private val subscriptionRepository: ISubscriptionRepository
) : ISubscriptionService {

    override suspend fun validateAndUpdateSubscriptionAsync(): Result<Boolean> {
        return try {
            // Platform-specific validation logic would go here
            Result.success(true)
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to validate and update subscription" }
            Result.failure("Billing service not available for validation")
        }
    }

    override suspend fun purchaseSubscriptionAsync(productId: String): Result<ProcessSubscriptionResultDto> {
        return try {
            // Platform-specific purchase implementation required
            Result.failure("Platform-specific billing implementation required")
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to purchase subscription" }
            Result.failure("There was an error processing your request, please try again")
        }
    }

    override suspend fun storeSubscriptionAsync(subscriptionData: ProcessSubscriptionResultDto): Result<Boolean> {
        return try {
            val currentTime = Clock.System.now().toEpochMilliseconds()

            val subscriptionDto = SubscriptionDto(
                id = 0,
                productId = subscriptionData.productId,
                transactionId = subscriptionData.transactionId,
                purchaseToken = subscriptionData.purchaseToken,
                isActive = subscriptionData.status.name == "ACTIVE",
                expirationDate = subscriptionData.expirationDate.toEpochMilliseconds(),
                purchaseDate = subscriptionData.purchaseDate.toEpochMilliseconds(),
                lastVerified = currentTime,
                userId = getUserId()
            )

            val result = withContext(Dispatchers.Default) {
                subscriptionRepository.addAsync(subscriptionDto)
            }

            Result.success(true)
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to store subscription" }
            Result.failure("Failed to store subscription data")
        }
    }

    override suspend fun getCurrentSubscriptionStatusAsync(): Result<SubscriptionStatusDto> {
        return try {
            val userId = getUserId()
            val currentTime = Clock.System.now().toEpochMilliseconds()

            val statusResult = withContext(Dispatchers.Default) {
                subscriptionRepository.getActiveByUserIdAsync(userId, currentTime)
            }

            val subscription = statusResult
            if (subscription == null) {
                return Result.success(SubscriptionStatusDto())
            }

            val now = Clock.System.now().toEpochMilliseconds()
            val isExpired = subscription.expirationDate < now
            val daysUntilExpiration = if (!isExpired) {
                ((subscription.expirationDate - now) / (24 * 60 * 60 * 1000)).toInt()
            } else 0

            val status = SubscriptionStatusDto(
                hasActiveSubscription = !isExpired && subscription.isActive,
                productId = subscription.productId,
                status = if (isExpired) com.x3squaredcircles.photography.domain.models.SubscriptionStatus.EXPIRED
                else com.x3squaredcircles.photography.domain.models.SubscriptionStatus.ACTIVE,
                expirationDate = kotlinx.datetime.Instant.fromEpochMilliseconds(subscription.expirationDate),
                purchaseDate = kotlinx.datetime.Instant.fromEpochMilliseconds(subscription.purchaseDate),
                period = com.x3squaredcircles.photography.domain.models.SubscriptionPeriod.MONTHLY, // Default, determine from productId
                isExpiringSoon = daysUntilExpiration <= 7 && daysUntilExpiration > 0,
                daysUntilExpiration = daysUntilExpiration
            )

            Result.success(status)
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get subscription status" }
            Result.failure("Failed to retrieve subscription status")
        }
    }

    override suspend fun getAvailableSubscriptionProductsAsync(): Result<List<String>> {
        return try {
            // Platform-specific product retrieval would go here
            val products = listOf(
                "premium_monthly_subscription",
                "premium_yearly_subscription"
            )
            Result.success(products)
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get available products" }
            Result.failure("No subscription products available")
        }
    }

    override suspend fun restoreSubscriptionsAsync(): Result<Boolean> {
        return try {
            // Platform-specific restore implementation required
            Result.success(true)
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to restore subscriptions" }
            Result.failure("Failed to restore subscriptions")
        }
    }

    private fun getUserId(): String {
        // This should be implemented with platform-specific secure storage
        return "default_user_id"
    }
}