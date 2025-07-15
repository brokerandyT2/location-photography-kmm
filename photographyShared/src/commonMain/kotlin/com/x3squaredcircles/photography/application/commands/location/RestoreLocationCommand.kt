// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/RestoreLocationCommand.kt
package com.x3squaredcircles.photography.application.commands.location

data class RestoreLocationCommand(
    val id: Int
)

data class RestoreLocationCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)