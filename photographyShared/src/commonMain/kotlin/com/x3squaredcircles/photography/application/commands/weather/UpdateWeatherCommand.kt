// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/weather/UpdateWeatherCommand.kt
package com.x3squaredcircles.photography.application.commands.weather

import com.x3squaredcircles.core.domain.entities.Weather

data class UpdateWeatherCommand(
    val locationId: Int,
    val forceUpdate: Boolean = false
)

data class UpdateWeatherCommandResult(
    val weather: Weather?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)