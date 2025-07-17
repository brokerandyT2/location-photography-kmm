// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/ISunService.kt
package com.x3squaredcircles.photography.domain.services

import kotlinx.datetime.Instant

interface ISunService {

    suspend fun getBulkSunDataAsync(
        dataTypes: List<String>,
        date: Instant,
        latitude: Double,
        longitude: Double,
        timezone: String
    ): Map<String, Instant>

    suspend fun getSunDataAsync(
        dataType: String,
        date: Instant,
        latitude: Double,
        longitude: Double,
        timezone: String
    ): Instant?

    fun cleanupExpiredCache()
}