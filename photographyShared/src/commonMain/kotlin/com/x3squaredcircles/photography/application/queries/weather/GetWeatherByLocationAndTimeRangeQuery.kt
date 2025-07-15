// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetWeatherByLocationAndTimeRangeQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.Weather
import kotlinx.datetime.Instant

data class GetWeatherByLocationAndTimeRangeQuery(
    val locationId: Int,
    val startTime: Instant,
    val endTime: Instant
)

data class GetWeatherByLocationAndTimeRangeQueryResult(
    val weather: List<Weather>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)