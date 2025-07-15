// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetAllWeatherQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

import com.x3squaredcircles.core.domain.entities.Weather

data class GetAllWeatherQuery(
    val dummy: Boolean = true
)

data class GetAllWeatherQueryResult(
    val weather: List<Weather>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)