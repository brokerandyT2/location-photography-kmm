// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetExpiredWeatherQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.Weather
import kotlinx.datetime.Instant

data class GetExpiredWeatherQuery(
    val olderThan: Instant
)

data class GetExpiredWeatherQueryResult(
    val weather: List<Weather>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)