// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/BulkDeleteLocationsCommand.kt
package com.x3squaredcircles.photography.application.commands.location

data class BulkDeleteLocationsCommand(
    val locationIds: List<Int>,
    val hardDelete: Boolean = false
)

data class BulkDeleteLocationsCommandResult(
    val deletedCount: Int,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)