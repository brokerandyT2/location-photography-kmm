// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/DeleteLensCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.DeleteLensCommand
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ILensRepository
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ILensCameraCompatibilityRepository

class DeleteLensCommandHandler(
    private val lensRepository: ILensRepository,
    private val compatibilityRepository: ILensCameraCompatibilityRepository
) : ICommandHandler<DeleteLensCommand, Result<Boolean>> {

    override suspend fun handle(request: DeleteLensCommand): Result<Boolean> {
        return try {
            val existsResult = lensRepository.getByIdAsync(request.id)
            if (!existsResult.isSuccess) {
                return Result.failure("Lens not found")
            }

            val lens = existsResult.getOrNull()
            if (lens == null) {
                return Result.failure("Lens not found")
            }

            compatibilityRepository.deleteByLensIdAsync(request.id)

            val deleteResult = lensRepository.deleteAsync(request.id)
            if (!deleteResult.isSuccess) {
                return Result.failure("Error deleting lens")
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure("Error deleting lens: ${e.message}")
        }
    }
}