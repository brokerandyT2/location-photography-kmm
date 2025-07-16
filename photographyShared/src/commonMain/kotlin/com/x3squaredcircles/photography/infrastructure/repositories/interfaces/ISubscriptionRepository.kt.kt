// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/interfaces/ISubscriptionRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories.interfaces

import com.x3squaredcircles.photography.application.queries.subscription.SubscriptionDto

interface ISubscriptionRepository {
    suspend fun getByIdAsync(id: Int): SubscriptionDto?
    suspend fun getAllAsync(): List<SubscriptionDto>
    suspend fun getActiveByUserIdAsync(userId: String, currentTime: Long): SubscriptionDto?
    suspend fun getByUserIdAsync(userId: String): List<SubscriptionDto>
    suspend fun getByTransactionIdAsync(transactionId: String): SubscriptionDto?
    suspend fun getByPurchaseTokenAsync(purchaseToken: String): SubscriptionDto?
    suspend fun getExpiredAsync(currentTime: Long): List<SubscriptionDto>
    suspend fun addAsync(subscription: SubscriptionDto): SubscriptionDto
    suspend fun updateAsync(subscription: SubscriptionDto)
    suspend fun updateStatusAsync(id: Int, isActive: Boolean, lastVerified: Long)
    suspend fun deactivateAsync(id: Int, lastVerified: Long)
    suspend fun deactivateExpiredAsync(currentTime: Long): Int
    suspend fun deleteAsync(id: Int)
    suspend fun getTotalCountAsync(): Long
    suspend fun getActiveCountAsync(currentTime: Long): Long
    suspend fun createBulkAsync(subscriptions: List<SubscriptionDto>): List<SubscriptionDto>
    suspend fun updateBulkAsync(subscriptions: List<SubscriptionDto>): Int
    suspend fun deleteBulkAsync(ids: List<Int>): Int
    fun clearCache()
    fun clearCache(id: Int)
    fun clearCache(userId: String)
}