// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ISubscriptionRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.photography.application.queries.subscription.SubscriptionDto
import com.x3squaredcircles.core.domain.common.Result

interface ISubscriptionRepository {
    suspend fun getByIdAsync(id: Int): Result<SubscriptionDto?>
    suspend fun getAllAsync(): Result<List<SubscriptionDto>>
    suspend fun getActiveByUserIdAsync(userId: String, currentTime: Long): Result<SubscriptionDto?>
    suspend fun getByUserIdAsync(userId: String): Result<List<SubscriptionDto>>
    suspend fun getByTransactionIdAsync(transactionId: String): Result<SubscriptionDto?>
    suspend fun getByPurchaseTokenAsync(purchaseToken: String): Result<SubscriptionDto?>
    suspend fun getExpiredAsync(currentTime: Long): Result<List<SubscriptionDto>>
    suspend fun createAsync(subscription: SubscriptionDto): Result<SubscriptionDto>
    suspend fun updateAsync(subscription: SubscriptionDto): Result<Unit>
    suspend fun updateStatusAsync(id: Int, isActive: Boolean, lastVerified: Long): Result<Unit>
    suspend fun deactivateAsync(id: Int, lastVerified: Long): Result<Unit>
    suspend fun deactivateExpiredAsync(currentTime: Long): Result<Int>
    suspend fun deleteAsync(id: Int): Result<Unit>
    suspend fun getTotalCountAsync(): Result<Long>
    suspend fun getActiveCountAsync(currentTime: Long): Result<Long>
    suspend fun createBulkAsync(subscriptions: List<SubscriptionDto>): Result<List<SubscriptionDto>>
    suspend fun updateBulkAsync(subscriptions: List<SubscriptionDto>): Result<Int>
    suspend fun deleteBulkAsync(ids: List<Int>): Result<Int>
    fun clearCache()
    fun clearCache(id: Int)
    fun clearCache(userId: String)
}