// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/weather/CleanupExpiredWeatherCommand.kt
package com.x3squaredcircles.photography.application.commands.weather

data class CleanupExpiredWeatherCommand(
    val olderThanTimestamp: Long
)

data class CleanupExpiredWeatherCommandResult(
    val cleanedUpCount: Int,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)