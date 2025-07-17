// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/ITimezoneService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

interface ITimezoneService {

    suspend fun getTimezoneAsync(latitude: Double, longitude: Double): Result<TimeZone>

    suspend fun convertToLocalTimeAsync(utcTime: Instant, timeZone: TimeZone): Result<Instant>

    suspend fun convertToUtcAsync(localTime: Instant, timeZone: TimeZone): Result<Instant>

    suspend fun getTimezoneOffsetAsync(timeZone: TimeZone, instant: Instant): Result<Int>

    suspend fun getTimezoneNameAsync(timeZone: TimeZone): Result<String>
}