// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetRecentWeatherQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.Weather

data class GetRecentWeatherQuery(
    val count: Int = 10
)

data class GetRecentWeatherQueryResult(
    val weather: List<Weather>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)