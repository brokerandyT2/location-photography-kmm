// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/ISubscriptionRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.photography.domain.entities.Subscription

/**
 * Repository interface for Subscription entity operations.
 */
interface ISubscriptionRepository {
    
    suspend fun getAllAsync(): Result<List<Subscription>>
    suspend fun getByIdAsync(id: Int): Result<Subscription>
    suspend fun getActiveByUserIdAsync(userId: String, currentTime: Long): Result<Subscription>
    suspend fun getByTransactionIdAsync(transactionId: String): Result<Subscription>
    suspend fun getByPurchaseTokenAsync(purchaseToken: String): Result<Subscription>
    suspend fun getExpiredAsync(currentTime: Long): Result<List<Subscription>>
    suspend fun getByUserIdAsync(userId: String): Result<List<Subscription>>
    suspend fun createAsync(subscription: Subscription): Result<Subscription>
    suspend fun updateAsync(subscription: Subscription): Result<Subscription>
    suspend fun updateStatusAsync(id: Int, isActive: Boolean, lastVerified: Long): Result<Boolean>
    suspend fun deactivateAsync(id: Int, lastVerified: Long): Result<Boolean>
    suspend fun deactivateExpiredAsync(currentTime: Long, lastVerified: Long): Result<Int>
    suspend fun deleteAsync(id: Int): Result<Boolean>
    suspend fun getCountAsync(): Result<Int>
    suspend fun getActiveCountAsync(currentTime: Long): Result<Int>
}