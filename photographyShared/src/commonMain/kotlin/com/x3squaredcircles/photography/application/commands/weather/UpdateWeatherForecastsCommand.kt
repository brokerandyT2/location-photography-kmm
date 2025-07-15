// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/weather/UpdateWeatherForecastsCommand.kt
package com.x3squaredcircles.photography.application.commands.weather

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class UpdateWeatherForecastsCommand(
    val weatherId: Int,
    val forecasts: List<WeatherForecastData>
)

data class WeatherForecastData(
    val date: Long,
    val sunrise: Long,
    val sunset: Long,
    val temperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val description: String,
    val icon: String,
    val windSpeed: Double,
    val windDirection: Double,
    val windGust: Double? = null,
    val humidity: Int,
    val pressure: Int,
    val clouds: Int,
    val uvIndex: Double,
    val precipitation: Double? = null,
    val moonRise: Long? = null,
    val moonSet: Long? = null,
    val moonPhase: Double = 0.0
)

data class UpdateWeatherForecastsCommandResult(
    val forecasts: List<WeatherForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)