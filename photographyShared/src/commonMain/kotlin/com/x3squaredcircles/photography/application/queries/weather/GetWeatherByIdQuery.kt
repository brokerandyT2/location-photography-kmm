// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetWeatherByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.Weather

data class GetWeatherByIdQuery(
    val id: Int
)

data class GetWeatherByIdQueryResult(
    val weather: Weather?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)