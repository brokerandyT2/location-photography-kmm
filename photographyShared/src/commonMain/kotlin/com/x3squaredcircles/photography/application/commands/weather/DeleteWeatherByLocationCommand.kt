// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/weather/DeleteWeatherByLocationCommand.kt
package com.x3squaredcircles.photography.application.commands.weather

data class DeleteWeatherByLocationCommand(
    val locationId: Int
)

data class DeleteWeatherByLocationCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)