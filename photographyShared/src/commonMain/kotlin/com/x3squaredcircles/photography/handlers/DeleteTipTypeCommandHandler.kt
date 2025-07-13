// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/DeleteTipTypeCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.DeleteTipTypeCommand
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ITipTypeRepository
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ITipRepository

class DeleteTipTypeCommandHandler(
    private val tipTypeRepository: ITipTypeRepository,
    private val tipRepository: ITipRepository
) : ICommandHandler<DeleteTipTypeCommand, Result<Boolean>> {

    override suspend fun handle(request: DeleteTipTypeCommand): Result<Boolean> {
        return try {
            val existsResult = tipTypeRepository.getByIdAsync(request.id)
            if (!existsResult.isSuccess) {
                return Result.failure("Tip type not found")
            }

            val tipType = existsResult.getOrNull()
            if (tipType == null) {
                return Result.failure("Tip type not found")
            }

            tipRepository.deleteByTypeAsync(request.id)

            val deleteResult = tipTypeRepository.deleteAsync(request.id)
            if (!deleteResult.isSuccess) {
                return Result.failure("Error deleting tip type")
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure("Error deleting tip type: ${e.message}")
        }
    }
}