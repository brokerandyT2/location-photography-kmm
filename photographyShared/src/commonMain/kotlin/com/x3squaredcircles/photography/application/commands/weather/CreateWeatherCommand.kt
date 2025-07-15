// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/weather/CreateWeatherCommand.kt
package com.x3squaredcircles.photography.application.commands.weather

import com.x3squaredcircles.core.domain.entities.Weather

data class CreateWeatherCommand(
    val locationId: Int,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val timezoneOffset: Int
)

data class CreateWeatherCommandResult(
    val weather: Weather,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)