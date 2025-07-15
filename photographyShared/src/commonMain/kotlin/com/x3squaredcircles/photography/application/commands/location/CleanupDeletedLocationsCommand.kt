// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/CleanupDeletedLocationsCommand.kt
package com.x3squaredcircles.photography.application.commands.location

data class CleanupDeletedLocationsCommand(
    val olderThanTimestamp: Long
)

data class CleanupDeletedLocationsCommandResult(
    val cleanedUpCount: Int,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)