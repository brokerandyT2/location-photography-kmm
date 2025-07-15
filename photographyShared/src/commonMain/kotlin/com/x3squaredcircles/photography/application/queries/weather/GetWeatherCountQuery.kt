// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/weather/GetWeatherCountQuery.kt
package com.x3squaredcircles.photography.application.queries.weather

data class GetWeatherCountQuery(
    val dummy: Boolean = true
)

data class GetWeatherCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)