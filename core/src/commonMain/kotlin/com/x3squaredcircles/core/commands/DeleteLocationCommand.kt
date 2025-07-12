// core/src/commonMain/kotlin/com/x3squaredcircles/core/commands/DeleteLocationCommand.kt
package com.x3squaredcircles.core.commands

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommand
import kotlinx.serialization.Serializable

/**
 * Command to delete a location (soft delete).
 */
@Serializable
data class DeleteLocationCommand(
    val id: Int
) : ICommand<Result<Boolean>> {
    
    init {
        require(id > 0) { "Location ID must be greater than 0" }
    }
}