// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/DeleteTipCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.DeleteTipCommand
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ITipRepository

class DeleteTipCommandHandler(
    private val tipRepository: ITipRepository
) : ICommandHandler<DeleteTipCommand, Result<Boolean>> {

    override suspend fun handle(request: DeleteTipCommand): Result<Boolean> {
        return try {
            val existsResult = tipRepository.getByIdAsync(request.id)
            if (!existsResult.isSuccess) {
                return Result.failure("Tip not found")
            }

            val tip = existsResult.getOrNull()
            if (tip == null) {
                return Result.failure("Tip not found")
            }

            val deleteResult = tipRepository.deleteAsync(request.id)
            if (!deleteResult.isSuccess) {
                return Result.failure("Error deleting tip")
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure("Error deleting tip: ${e.message}")
        }
    }
}