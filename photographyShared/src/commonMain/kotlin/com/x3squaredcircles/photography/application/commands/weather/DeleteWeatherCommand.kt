// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/weather/DeleteWeatherCommand.kt
package com.x3squaredcircles.photography.application.commands.weather

data class DeleteWeatherCommand(
    val id: Int,
    val hardDelete: Boolean = false
)

data class DeleteWeatherCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)