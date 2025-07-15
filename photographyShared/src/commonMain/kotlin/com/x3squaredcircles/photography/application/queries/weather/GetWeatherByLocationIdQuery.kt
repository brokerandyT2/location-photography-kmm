// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetWeatherByLocationIdQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.Weather

data class GetWeatherByLocationIdQuery(
    val locationId: Int
)

data class GetWeatherByLocationIdQueryResult(
    val weather: Weather?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)