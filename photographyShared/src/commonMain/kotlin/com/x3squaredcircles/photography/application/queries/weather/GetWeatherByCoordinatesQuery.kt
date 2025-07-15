// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetWeatherByCoordinatesQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.Weather

data class GetWeatherByCoordinatesQuery(
    val latitude: Double,
    val longitude: Double
)

data class GetWeatherByCoordinatesQueryResult(
    val weather: Weather?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)