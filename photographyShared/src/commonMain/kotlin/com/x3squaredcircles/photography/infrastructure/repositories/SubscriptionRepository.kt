// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/SubscriptionRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService

import com.x3squaredcircles.photography.domain.entities.Subscription
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import java.util.concurrent.ConcurrentHashMap
class SubscriptionRepository(
    private val database: PhotographyDatabase,
    private val logger: ILoggingService
) : ISubscriptionRepository {
    private val subscriptionCache = ConcurrentHashMap<String, Pair<Subscription?, Long>>()
    private val cacheTimeoutMinutes = 5L

    override suspend fun getAllAsync(): Result<List<Subscription>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.subscriptionQueries.selectAll().executeAsList()
                val subscriptions = entities.map { entity ->
                    Subscription(
                        id = entity.id.toInt(),
                        userId = entity.userId,
                        transactionId = entity.transactionId,
                        purchaseToken = entity.purchaseToken,
                        productId = entity.productId,
                        isActive = entity.isActive == 1L,
                        expirationDate = entity.expirationDate,
                        purchaseDate = entity.purchaseDate,
                        lastVerified = entity.lastVerified
                    )
                }
                Result.success(subscriptions)
            }
        } catch (e: Exception) {
            logger.logError("Error getting all subscriptions", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getByIdAsync(id: Int): Result<Subscription> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.subscriptionQueries.selectById(id.toLong()).executeAsOneOrNull()
                if (entity != null) {
                    val subscription = Subscription(
                        id = entity.id.toInt(),
                        userId = entity.userId,
                        transactionId = entity.transactionId,
                        purchaseToken = entity.purchaseToken,
                        productId = entity.productId,
                        isActive = entity.isActive == 1L,
                        expirationDate = entity.expirationDate,
                        purchaseDate = entity.purchaseDate,
                        lastVerified = entity.lastVerified
                    )
                    Result.success(subscription)
                } else {
                    Result.failure("Subscription not found")
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting subscription by ID: $id", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getActiveByUserIdAsync(userId: String, currentTime: Long): Result<Subscription> {
        return try {
            val cacheKey = "active_$userId"
            val cached = subscriptionCache[cacheKey]
            if (cached != null && Clock.System.now().epochSeconds < cached.second + (cacheTimeoutMinutes * 60)) {
                cached.first?.let {
                    return Result.success(it)
                }
            }

            withContext(Dispatchers.IO) {
                val entity = database.subscriptionQueries.selectActiveByUserId(userId, currentTime).executeAsOneOrNull()
                if (entity != null) {
                    val subscription = Subscription(
                        id = entity.id.toInt(),
                        userId = entity.userId,
                        transactionId = entity.transactionId,
                        purchaseToken = entity.purchaseToken,
                        productId = entity.productId,
                        isActive = entity.isActive == 1L,
                        expirationDate = entity.expirationDate,
                        purchaseDate = entity.purchaseDate,
                        lastVerified = entity.lastVerified
                    )

                    val cacheExpiry = Clock.System.now().epochSeconds + (cacheTimeoutMinutes * 60)
                    subscriptionCache[cacheKey] = Pair(subscription, cacheExpiry)

                    Result.success(subscription)
                } else {
                    subscriptionCache[cacheKey] = Pair(null, Clock.System.now().epochSeconds + (cacheTimeoutMinutes * 60))
                    Result.failure("No active subscription found")
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting active subscription for user: $userId", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getByTransactionIdAsync(transactionId: String): Result<Subscription> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.subscriptionQueries.selectByTransactionId(transactionId).executeAsOneOrNull()
                if (entity != null) {
                    val subscription = Subscription(
                        id = entity.id.toInt(),
                        userId = entity.userId,
                        transactionId = entity.transactionId,
                        purchaseToken = entity.purchaseToken,
                        productId = entity.productId,
                        isActive = entity.isActive == 1L,
                        expirationDate = entity.expirationDate,
                        purchaseDate = entity.purchaseDate,
                        lastVerified = entity.lastVerified
                    )
                    Result.success(subscription)
                } else {
                    Result.failure("Subscription not found")
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting subscription by transaction ID: $transactionId", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getByPurchaseTokenAsync(purchaseToken: String): Result<Subscription> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.subscriptionQueries.selectByPurchaseToken(purchaseToken).executeAsOneOrNull()
                if (entity != null) {
                    val subscription = Subscription(
                        id = entity.id.toInt(),
                        userId = entity.userId,
                        transactionId = entity.transactionId,
                        purchaseToken = entity.purchaseToken,
                        productId = entity.productId,
                        isActive = entity.isActive == 1L,
                        expirationDate = entity.expirationDate,
                        purchaseDate = entity.purchaseDate,
                        lastVerified = entity.lastVerified
                    )
                    Result.success(subscription)
                } else {
                    Result.failure("Subscription not found")
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting subscription by purchase token: $purchaseToken", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getExpiredAsync(currentTime: Long): Result<List<Subscription>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.subscriptionQueries.selectExpired(currentTime).executeAsList()
                val subscriptions = entities.map { entity ->
                    Subscription(
                        id = entity.id.toInt(),
                        userId = entity.userId,
                        transactionId = entity.transactionId,
                        purchaseToken = entity.purchaseToken,
                        productId = entity.productId,
                        isActive = entity.isActive == 1L,
                        expirationDate = entity.expirationDate,
                        purchaseDate = entity.purchaseDate,
                        lastVerified = entity.lastVerified
                    )
                }
                Result.success(subscriptions)
            }
        } catch (e: Exception) {
            logger.logError("Error getting expired subscriptions", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getByUserIdAsync(userId: String): Result<List<Subscription>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.subscriptionQueries.selectByUserId(userId).executeAsList()
                val subscriptions = entities.map { entity ->
                    Subscription(
                        id = entity.id.toInt(),
                        userId = entity.userId,
                        transactionId = entity.transactionId,
                        purchaseToken = entity.purchaseToken,
                        productId = entity.productId,
                        isActive = entity.isActive == 1L,
                        expirationDate = entity.expirationDate,
                        purchaseDate = entity.purchaseDate,
                        lastVerified = entity.lastVerified
                    )
                }
                Result.success(subscriptions)
            }
        } catch (e: Exception) {
            logger.logError("Error getting subscriptions for user: $userId", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun createAsync(subscription: Subscription): Result<Subscription> {
        return try {
            withContext(Dispatchers.IO) {
                database.subscriptionQueries.insert(
                    userId = subscription.userId,
                    transactionId = subscription.transactionId,
                    purchaseToken = subscription.purchaseToken,
                    productId = subscription.productId,
                    isActive = if (subscription.isActive) 1L else 0L,
                    expirationDate = subscription.expirationDate,
                    purchaseDate = subscription.purchaseDate,
                    lastVerified = subscription.lastVerified
                )

                val insertedId = database.subscriptionQueries.transactionWithResult {
                    database.subscriptionQueries.selectAll().executeAsList().lastOrNull()?.id?.toInt() ?: 0
                }

                val newSubscription = subscription.copy(id = insertedId)

                invalidateCacheForUser(subscription.userId)

                logger.logInfo("Created subscription with ID: $insertedId")
                Result.success(newSubscription)
            }
        } catch (e: Exception) {
            logger.logError("Error creating subscription", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun updateAsync(subscription: Subscription): Result<Subscription> {
        return try {
            withContext(Dispatchers.IO) {
                database.subscriptionQueries.update(
                    userId = subscription.userId,
                    transactionId = subscription.transactionId,
                    purchaseToken = subscription.purchaseToken,
                    productId = subscription.productId,
                    isActive = if (subscription.isActive) 1L else 0L,
                    expirationDate = subscription.expirationDate,
                   lastVerified = subscription.lastVerified,
                    id = subscription.id.toLong()
                )

                invalidateCacheForUser(subscription.userId)

                logger.logInfo("Updated subscription with ID: ${subscription.id}")
                Result.success(subscription)
            }
        } catch (e: Exception) {
            logger.logError("Error updating subscription", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun updateStatusAsync(id: Int, isActive: Boolean, lastVerified: Long): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.subscriptionQueries.updateStatus(
                    isActive = if (isActive) 1L else 0L,
                    lastVerified = lastVerified,
                    id = id.toLong()
                )

                val subscription = database.subscriptionQueries.selectById(id.toLong()).executeAsOneOrNull()
                subscription?.let {
                    invalidateCacheForUser(it.userId)
                }

                logger.logInfo("Updated subscription status for ID: $id")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error updating subscription status", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun deactivateAsync(id: Int, lastVerified: Long): Result<Boolean> {
        return updateStatusAsync(id, false, lastVerified)
    }

    override suspend fun deactivateExpiredAsync(currentTime: Long, lastVerified: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val expiredSubscriptions = database.subscriptionQueries.selectExpired(currentTime).executeAsList()
                var deactivatedCount = 0

                database.transaction {
                    expiredSubscriptions.forEach { subscription ->
                        database.subscriptionQueries.updateStatus(
                            isActive = 0L,
                            lastVerified = lastVerified,
                            id = subscription.id
                        )
                        invalidateCacheForUser(subscription.userId)
                        deactivatedCount++
                    }
                }

                logger.logInfo("Deactivated $deactivatedCount expired subscriptions")
                Result.success(deactivatedCount)
            }
        } catch (e: Exception) {
            logger.logError("Error deactivating expired subscriptions", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                val subscription = database.subscriptionQueries.selectById(id.toLong()).executeAsOneOrNull()
                database.subscriptionQueries.deleteById(id.toLong())

                subscription?.let {
                    invalidateCacheForUser(it.userId)
                }

                logger.logInfo("Deleted subscription with ID: $id")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting subscription", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.subscriptionQueries.getCount().executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting subscription count", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getActiveCountAsync(currentTime: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.subscriptionQueries.getActiveCount(currentTime).executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting active subscription count", e)
            Result.failure(e.message!!)
        }
    }

    private fun invalidateCacheForUser(userId: String) {
        subscriptionCache.keys.removeAll { key ->
            key.contains(userId)
        }
    }
}