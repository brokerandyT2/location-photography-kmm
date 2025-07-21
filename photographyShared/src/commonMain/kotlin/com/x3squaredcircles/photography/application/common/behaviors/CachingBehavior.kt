// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/common/behaviors/CachingBehavior.kt
package com.x3squaredcircles.photography.application.common.behaviors

import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

/**
 * Caching behavior for frequently accessed read-only data to improve performance
 */
class CachingBehavior<TRequest, TResponse>(
    private val logger: Logger
) : IPipelineBehavior<TRequest, TResponse>
        where TRequest : Any,
              TResponse : Any {

    companion object {
        private val cache = mutableMapOf<String, CacheEntry>()
        private val defaultCacheTime = 5.minutes
        private const val MAX_CACHE_SIZE = 1000

        /**
         * Cleanup expired cache entries to prevent memory leaks
         */
        fun cleanupExpiredCache() {
            synchronized(cache) {
                val now = Clock.System.now()
                val expiredKeys = cache.filter { (_, entry) ->
                    now > entry.expiry
                }.keys.toList()

                expiredKeys.forEach { key ->
                    cache.remove(key)
                }

                // Also cleanup if cache is too large
                if (cache.size > MAX_CACHE_SIZE) {
                    val oldestEntries = cache.entries
                        .sortedBy { it.value.created }
                        .take(cache.size - MAX_CACHE_SIZE + 100) // Remove extra to avoid frequent cleanups

                    oldestEntries.forEach { (key, _) ->
                        cache.remove(key)
                    }
                }
            }
        }

        /**
         * Clear all cached entries
         */
        fun clearCache() {
            synchronized(cache) {
                cache.clear()
            }
        }

        /**
         * Get cache statistics
         */
        fun getCacheStatistics(): CacheStatistics {
            synchronized(cache) {
                val now = Clock.System.now()
                val activeEntries = cache.count { (_, entry) -> now <= entry.expiry }
                val expiredEntries = cache.size - activeEntries

                return CacheStatistics(
                    totalEntries = cache.size,
                    activeEntries = activeEntries,
                    expiredEntries = expiredEntries,
                    memoryFootprint = estimateMemoryFootprint()
                )
            }
        }

        private fun estimateMemoryFootprint(): Long {
            // Simple estimation - in a real implementation, this would be more sophisticated
            return cache.size * 1024L // Assume ~1KB per entry average
        }
    }

    override suspend fun handle(
        request: TRequest,
        next: suspend () -> TResponse
    ): TResponse {
        val requestName = request::class.simpleName ?: "Unknown"

        // Only cache read-only queries, not commands
        if (!requestName.endsWith("Query")) {
            logger.v { "Skipping cache for command: $requestName" }
            return next()
        }

        val cacheKey = generateCacheKey(requestName, request)

        // Check cache first
        synchronized(cache) {
            cache[cacheKey]?.let { cachedEntry ->
                val now = Clock.System.now()
                if (now <= cachedEntry.expiry) {
                    logger.d { "Cache hit for $requestName" }
                    cachedEntry.hitCount++
                    cachedEntry.lastAccessed = now

                    @Suppress("UNCHECKED_CAST")
                    return cachedEntry.response as TResponse
                } else {
                    // Remove expired entry
                    cache.remove(cacheKey)
                    logger.v { "Removed expired cache entry for $requestName" }
                }
            }
        }

        logger.v { "Cache miss for $requestName - executing request" }

        // Execute request and cache result
        val response = next()

        // Cache successful responses (assuming null indicates failure)
        if (response != null) {
            val now = Clock.System.now()
            val cacheEntry = CacheEntry(
                response = response,
                expiry = now + defaultCacheTime.inWholeMilliseconds,
                created = now,
                lastAccessed = now,
                hitCount = 0
            )

            synchronized(cache) {
                cache[cacheKey] = cacheEntry
                logger.d { "Cached response for $requestName (cache size: ${cache.size})" }

                // Cleanup if cache is getting too large
                if (cache.size > MAX_CACHE_SIZE * 1.2) { // 20% buffer before cleanup
                    cleanupExpiredCache()
                }
            }
        } else {
            logger.v { "Not caching null response for $requestName" }
        }

        return response
    }

    private fun generateCacheKey(requestName: String, request: TRequest): String {
        // Create a cache key based on request type and its properties
        val requestHash = request.hashCode()
        return "${requestName}_$requestHash"
    }
}

/**
 * Represents a cached entry with metadata
 */
private data class CacheEntry(
    val response: Any,
    val expiry: Instant,
    val created: Instant,
    var lastAccessed: Instant,
    var hitCount: Int
)

/**
 * Extension to convert Duration to Instant offset
 */
private operator fun Instant.plus(milliseconds: Long): Instant {
    return Instant.fromEpochMilliseconds(this.toEpochMilliseconds() + milliseconds)
}

/**
 * Cache statistics for monitoring and debugging
 */
data class CacheStatistics(
    val totalEntries: Int,
    val activeEntries: Int,
    val expiredEntries: Int,
    val memoryFootprint: Long
)

/**
 * Cache configuration options
 */
data class CacheConfiguration(
    val defaultTtlMinutes: Int = 5,
    val maxCacheSize: Int = 1000,
    val enableStatistics: Boolean = true,
    val cleanupIntervalMinutes: Int = 10
) {
    companion object {
        val default = CacheConfiguration()

        val conservative = CacheConfiguration(
            defaultTtlMinutes = 2,
            maxCacheSize = 500,
            cleanupIntervalMinutes = 5
        )

        val aggressive = CacheConfiguration(
            defaultTtlMinutes = 15,
            maxCacheSize = 2000,
            cleanupIntervalMinutes = 20
        )
    }
}

/**
 * Cache management utilities
 */
object CacheManager {

    /**
     * Performs cache maintenance operations
     */
    suspend fun performMaintenance() {
        try {
            CachingBehavior.cleanupExpiredCache()

            val stats = CachingBehavior.getCacheStatistics()

            // Log cache health metrics
            val logger = Logger.withTag("CacheManager")
            logger.d {
                "Cache maintenance completed - " +
                        "Active: ${stats.activeEntries}, " +
                        "Expired: ${stats.expiredEntries}, " +
                        "Memory: ${stats.memoryFootprint / 1024}KB"
            }

            // Warn if cache is inefficient
            if (stats.expiredEntries > stats.activeEntries && stats.totalEntries > 100) {
                logger.w {
                    "Cache inefficiency detected - " +
                            "more expired entries (${stats.expiredEntries}) than active (${stats.activeEntries}). " +
                            "Consider adjusting TTL or cleanup frequency."
                }
            }

        } catch (ex: Exception) {
            val logger = Logger.withTag("CacheManager")
            logger.e(ex) { "Error during cache maintenance" }
        }
    }

    /**
     * Forces immediate cache cleanup
     */
    fun forceCleanup() {
        CachingBehavior.cleanupExpiredCache()
    }

    /**
     * Clears all cached data
     */
    fun clearAll() {
        CachingBehavior.clearCache()
    }
}