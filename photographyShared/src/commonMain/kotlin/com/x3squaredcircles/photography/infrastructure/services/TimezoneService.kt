// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/TimezoneService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.services.ITimezoneService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.Clock
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours

class TimezoneService(
    private val logger: Logger
) : ITimezoneService {

    companion object {
        private val timezoneCache = mutableMapOf<String, CacheEntry>()
        private val cacheTimeout = 24.hours
        private const val MAX_CACHE_SIZE = 1000
    }

    data class CacheEntry(
        val timezone: TimeZone,
        val expiry: Instant
    )

    data class TimezoneBounds(
        val minLatitude: Double,
        val maxLatitude: Double,
        val minLongitude: Double,
        val maxLongitude: Double
    ) {
        fun contains(latitude: Double, longitude: Double): Boolean {
            return latitude in minLatitude..maxLatitude &&
                    longitude in minLongitude..maxLongitude
        }

        val area: Double get() = (maxLatitude - minLatitude) * (maxLongitude - minLongitude)
    }

    private val timezoneBounds = mapOf(
        // US Timezones
        "America/New_York" to TimezoneBounds(25.0, 49.0, -84.0, -67.0),
        "America/Chicago" to TimezoneBounds(25.0, 49.0, -104.0, -84.0),
        "America/Denver" to TimezoneBounds(25.0, 49.0, -114.0, -104.0),
        "America/Los_Angeles" to TimezoneBounds(25.0, 49.0, -125.0, -114.0),
        "America/Anchorage" to TimezoneBounds(55.0, 71.0, -180.0, -130.0),
        "Pacific/Honolulu" to TimezoneBounds(18.0, 23.0, -162.0, -154.0),

        // Major international zones
        "Europe/London" to TimezoneBounds(49.0, 61.0, -8.0, 2.0),
        "Europe/Berlin" to TimezoneBounds(47.0, 55.0, 6.0, 15.0),
        "Europe/Moscow" to TimezoneBounds(55.0, 68.0, 37.0, 40.0),
        "Asia/Tokyo" to TimezoneBounds(30.0, 46.0, 129.0, 146.0),
        "Asia/Shanghai" to TimezoneBounds(18.0, 54.0, 73.0, 135.0),
        "Australia/Sydney" to TimezoneBounds(-44.0, -10.0, 113.0, 154.0),

        // Additional global coverage
        "America/Mexico_City" to TimezoneBounds(14.0, 33.0, -118.0, -86.0),
        "America/Toronto" to TimezoneBounds(42.0, 84.0, -95.0, -74.0),
        "Europe/Paris" to TimezoneBounds(41.0, 51.0, -5.0, 10.0),
        "Asia/Kolkata" to TimezoneBounds(6.0, 38.0, 68.0, 97.0),
        "Africa/Cairo" to TimezoneBounds(22.0, 32.0, 25.0, 35.0),
        "America/Sao_Paulo" to TimezoneBounds(-34.0, 5.0, -74.0, -35.0),
        "Asia/Dubai" to TimezoneBounds(22.0, 26.0, 51.0, 56.0),
        "Pacific/Auckland" to TimezoneBounds(-47.0, -34.0, 166.0, 179.0),
        "America/Vancouver" to TimezoneBounds(49.0, 60.0, -139.0, -114.0),
        "Europe/Rome" to TimezoneBounds(36.0, 47.0, 6.0, 19.0),
        "Asia/Seoul" to TimezoneBounds(33.0, 39.0, 124.0, 132.0),
        "America/Lima" to TimezoneBounds(-18.0, 0.0, -81.0, -69.0),
        "Africa/Johannesburg" to TimezoneBounds(-35.0, -22.0, 16.0, 33.0),
        "Asia/Bangkok" to TimezoneBounds(5.0, 21.0, 97.0, 106.0),
        "Europe/Stockholm" to TimezoneBounds(55.0, 70.0, 11.0, 24.0)
    )

    override suspend fun getTimezoneAsync(latitude: Double, longitude: Double): Result<TimeZone> {
        return withContext(Dispatchers.Default) {
            try {
                // Validate coordinates
                if (!isValidCoordinate(latitude, longitude)) {
                    return@withContext Result.failure("Invalid coordinates: latitude=$latitude, longitude=$longitude")
                }

                val cacheKey = "${latitude.format(4)}_${longitude.format(4)}"

                // Check cache first
                timezoneCache[cacheKey]?.let { cached ->
                    val now = Clock.System.now()
                    if (now < cached.expiry) {
                        logger.d { "Cache hit for timezone lookup: $cacheKey" }
                        return@withContext Result.success(cached.timezone)
                    } else {
                        timezoneCache.remove(cacheKey)
                    }
                }

                // Find timezone for coordinates
                val timezoneId = findTimezoneForCoordinates(latitude, longitude)
                val timezone = try {
                    TimeZone.of(timezoneId)
                } catch (e: Exception) {
                    logger.w(e) { "Failed to create timezone for ID: $timezoneId, falling back to UTC" }
                    TimeZone.UTC
                }

                // Cache the result
                val now = Clock.System.now()
                val cacheEntry = CacheEntry(
                    timezone = timezone,
                    expiry = now + cacheTimeout.inWholeMilliseconds
                )

                synchronized(timezoneCache) {
                    // Manage cache size
                    if (timezoneCache.size >= MAX_CACHE_SIZE) {
                        cleanupExpiredCache()
                        if (timezoneCache.size >= MAX_CACHE_SIZE) {
                            // Remove oldest entries
                            val oldestKeys = timezoneCache.entries
                                .sortedBy { it.value.expiry }
                                .take(MAX_CACHE_SIZE / 4)
                                .map { it.key }
                            oldestKeys.forEach { timezoneCache.remove(it) }
                        }
                    }
                    timezoneCache[cacheKey] = cacheEntry
                }

                logger.d { "Timezone lookup successful: $cacheKey -> ${timezone.id}" }
                Result.success(timezone)

            } catch (ex: Exception) {
                logger.e(ex) { "Error determining timezone for coordinates: $latitude, $longitude" }
                Result.failure("Error determining timezone: ${ex.message}")
            }
        }
    }

    override suspend fun convertToLocalTimeAsync(utcTime: Instant, timeZone: TimeZone): Result<Instant> {
        return try {
            // In Kotlin DateTime, timezone conversion doesn't change the Instant value
            // The Instant represents the same moment in time regardless of timezone
            // Conversion happens when displaying/formatting the time
            Result.success(utcTime)
        } catch (ex: Exception) {
            logger.e(ex) { "Error converting UTC time to local time" }
            Result.failure("Error converting time: ${ex.message}")
        }
    }

    override suspend fun convertToUtcAsync(localTime: Instant, timeZone: TimeZone): Result<Instant> {
        return try {
            // Similar to convertToLocalTimeAsync, Instant is timezone-agnostic
            // The conversion logic would depend on how the localTime was interpreted
            Result.success(localTime)
        } catch (ex: Exception) {
            logger.e(ex) { "Error converting local time to UTC" }
            Result.failure("Error converting time: ${ex.message}")
        }
    }

    override suspend fun getTimezoneOffsetAsync(timeZone: TimeZone, instant: Instant): Result<Int> {
        return try {
            // Convert instant to local date time in the specified timezone
            val localDateTime = instant.toLocalDateTime(timeZone)

            // Convert back to instant to calculate the offset
            val utcInstant = localDateTime.toInstant(TimeZone.UTC)
            val localInstant = localDateTime.toInstant(timeZone)

            // Calculate offset in seconds
            val offsetSeconds = (localInstant.toEpochMilliseconds() - utcInstant.toEpochMilliseconds()) / 1000

            Result.success(offsetSeconds.toInt())
        } catch (ex: Exception) {
            logger.e(ex) { "Error getting timezone offset" }
            Result.failure("Error getting timezone offset: ${ex.message}")
        }
    }

    override suspend fun getTimezoneNameAsync(timeZone: TimeZone): Result<String> {
        return try {
            Result.success(timeZone.id)
        } catch (ex: Exception) {
            logger.e(ex) { "Error getting timezone name" }
            Result.failure("Error getting timezone name: ${ex.message}")
        }
    }

    private fun findTimezoneForCoordinates(latitude: Double, longitude: Double): String {
        // Find matching timezone bounds
        val matchingTimezones = timezoneBounds.filter { (_, bounds) ->
            bounds.contains(latitude, longitude)
        }

        return when {
            matchingTimezones.isNotEmpty() -> {
                // If multiple matches, prefer the one with the smallest area (most specific)
                matchingTimezones.minByOrNull { it.value.area }?.key ?: "UTC"
            }
            else -> {
                // Fallback: rough timezone calculation based on longitude
                getTimezoneByUtcOffset(kotlin.math.round(longitude / 15.0).toInt())
            }
        }
    }

    private fun getTimezoneByUtcOffset(utcOffset: Int): String {
        return when (utcOffset) {
            -10 -> "Pacific/Honolulu"
            -9 -> "America/Anchorage"
            -8 -> "America/Los_Angeles"
            -7 -> "America/Denver"
            -6 -> "America/Chicago"
            -5 -> "America/New_York"
            -4 -> "America/Halifax"
            -3 -> "America/Argentina/Buenos_Aires"
            0 -> "UTC"
            1 -> "Europe/London"
            2 -> "Europe/Berlin"
            3 -> "Europe/Moscow"
            6 -> "Asia/Dhaka"
            8 -> "Asia/Shanghai"
            9 -> "Asia/Tokyo"
            10 -> "Australia/Sydney"
            else -> "UTC"
        }
    }

    private fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }

    private fun Double.format(decimals: Int): String {
        return "%.${decimals}f".format(this)
    }

    private fun cleanupExpiredCache() {
        val now = Clock.System.now()
        val expiredKeys = timezoneCache.filter { (_, entry) ->
            now >= entry.expiry
        }.keys.toList()

        expiredKeys.forEach { key ->
            timezoneCache.remove(key)
        }

        logger.d { "Cleaned up ${expiredKeys.size} expired timezone cache entries" }
    }

    /**
     * Get batch timezone lookups for multiple coordinates
     */
    suspend fun getBatchTimezonesAsync(
        coordinates: List<Pair<Double, Double>>
    ): Result<Map<String, TimeZone>> {
        return withContext(Dispatchers.Default) {
            try {
                if (coordinates.isEmpty()) {
                    return@withContext Result.success(emptyMap())
                }

                val results = mutableMapOf<String, TimeZone>()

                for ((latitude, longitude) in coordinates) {
                    val cacheKey = "${latitude.format(4)}_${longitude.format(4)}"
                    when (val result = getTimezoneAsync(latitude, longitude)) {
                        is Result.Success -> {
                            results[cacheKey] = result.data
                        }
                        is Result.Failure -> {
                            logger.w { "Failed to get timezone for $cacheKey: ${result.error}" }
                            results[cacheKey] = TimeZone.UTC
                        }
                    }
                }

                Result.success(results.toMap())
            } catch (ex: Exception) {
                logger.e(ex) { "Error in batch timezone lookup" }
                Result.failure("Error in batch timezone lookup: ${ex.message}")
            }
        }
    }

    /**
     * Preload common timezone lookups for better performance
     */
    suspend fun preloadCommonTimezones() {
        val commonCoordinates = listOf(
            40.7128 to -74.0060, // New York
            34.0522 to -118.2437, // Los Angeles
            41.8781 to -87.6298, // Chicago
            51.5074 to -0.1278, // London
            48.8566 to 2.3522, // Paris
            35.6762 to 139.6503, // Tokyo
            -33.8688 to 151.2093, // Sydney
            55.7558 to 37.6176 // Moscow
        )

        logger.d { "Preloading ${commonCoordinates.size} common timezone lookups" }

        for ((latitude, longitude) in commonCoordinates) {
            getTimezoneAsync(latitude, longitude)
        }

        logger.d { "Completed preloading common timezone lookups" }
    }

    /**
     * Get cache statistics for monitoring
     */
    fun getCacheStatistics(): Map<String, Int> {
        val now = Clock.System.now()
        val activeEntries = timezoneCache.count { (_, entry) -> now < entry.expiry }
        val expiredEntries = timezoneCache.size - activeEntries

        return mapOf(
            "totalEntries" to timezoneCache.size,
            "activeEntries" to activeEntries,
            "expiredEntries" to expiredEntries
        )
    }
}

/**
 * Extension for Instant to add timezone-aware milliseconds conversion
 */
private operator fun Instant.plus(milliseconds: Long): Instant {
    return Instant.fromEpochMilliseconds(this.toEpochMilliseconds() + milliseconds)
}