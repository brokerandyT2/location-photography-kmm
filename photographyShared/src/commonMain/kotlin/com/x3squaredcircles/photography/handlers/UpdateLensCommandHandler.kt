// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/UpdateLensCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.UpdateLensCommand
import com.x3squaredcircles.photography.dtos.LensDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ILensRepository

class UpdateLensCommandHandler(
    private val lensRepository: ILensRepository
) : ICommandHandler<UpdateLensCommand, Result<LensDto>> {

    override suspend fun handle(request: UpdateLensCommand): Result<LensDto> {
        return try {
            val existingResult = lensRepository.getByIdAsync(request.id)
            if (!existingResult.isSuccess) {
                return Result.failure("Lens not found")
            }

            val existingLens = existingResult.getOrNull()
            if (existingLens == null) {
                return Result.failure("Lens not found")
            }

            val updatedLens = existingLens.copy(
                minMM = request.minMM,
                maxMM = request.maxMM ?: request.minMM,
                minFStop = request.minFStop ?: 1.4,
                maxFStop = request.maxFStop ?: request.minFStop ?: 22.0,
                isPrime = request.maxMM == null || request.maxMM == request.minMM,
                nameForLens = request.lensName
            )

            val updateResult = lensRepository.updateAsync(updatedLens)
            if (!updateResult.isSuccess) {
                return Result.failure("Error updating lens")
            }

            val savedLens = updateResult.getOrNull()!!
            val dto = LensDto(
                id = savedLens.id,
                minMM = savedLens.minMM,
                maxMM = savedLens.maxMM,
                minFStop = savedLens.minFStop,
                maxFStop = savedLens.maxFStop,
                isPrime = savedLens.isPrime,
                isUserCreated = savedLens.isUserCreated,
                dateAdded = savedLens.dateAdded,
                displayName = savedLens.nameForLens
            )

            Result.success(dto)
        } catch (e: Exception) {
            Result.failure("Error updating lens: ${e.message}")
        }
    }
}