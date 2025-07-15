// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/weather/UpdateHourlyForecastsCommand.kt
package com.x3squaredcircles.photography.application.commands.weather

import com.x3squaredcircles.core.domain.entities.HourlyForecast

data class UpdateHourlyForecastsCommand(
    val weatherId: Int,
    val hourlyForecasts: List<HourlyForecastData>
)

data class HourlyForecastData(
    val dateTime: Long,
    val temperature: Double,
    val feelsLike: Double,
    val description: String,
    val icon: String,
    val windSpeed: Double,
    val windDirection: Double,
    val windGust: Double? = null,
    val humidity: Int,
    val pressure: Int,
    val clouds: Int,
    val uvIndex: Double,
    val probabilityOfPrecipitation: Double,
    val visibility: Int,
    val dewPoint: Double
)

data class UpdateHourlyForecastsCommandResult(
    val hourlyForecasts: List<HourlyForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)