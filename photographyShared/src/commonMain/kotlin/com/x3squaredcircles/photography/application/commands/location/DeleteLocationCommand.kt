// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/location/DeleteLocationCommand.kt
package com.x3squaredcircles.photography.application.commands.location

data class DeleteLocationCommand(
    val id: Int,
    val hardDelete: Boolean = false
)

data class DeleteLocationCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)