// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/UpdateTipTypeCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.UpdateTipTypeCommand
import com.x3squaredcircles.photography.dtos.TipTypeDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ITipTypeRepository

class UpdateTipTypeCommandHandler(
    private val tipTypeRepository: ITipTypeRepository
) : ICommandHandler<UpdateTipTypeCommand, Result<TipTypeDto>> {

    override suspend fun handle(request: UpdateTipTypeCommand): Result<TipTypeDto> {
        return try {
            val existingResult = tipTypeRepository.getByIdAsync(request.id)
            if (!existingResult.isSuccess) {
                return Result.failure("Tip type not found")
            }

            val existingTipType = existingResult.getOrNull()
            if (existingTipType == null) {
                return Result.failure("Tip type not found")
            }

            val nameExistsResult = tipTypeRepository.existsByNameAsync(request.name, request.id)
            if (nameExistsResult.isSuccess && nameExistsResult.getOrNull() == true) {
                return Result.failure("TipType with this name already exists")
            }

            val updatedTipType = existingTipType.copy(
                name = request.name

            )

            val updateResult = tipTypeRepository.updateAsync(updatedTipType)
            if (!updateResult.isSuccess) {
                return Result.failure("Error updating tip type")
            }

            val savedTipType = updateResult.getOrNull()!!
            val dto = TipTypeDto(
                id = savedTipType.id,
                name = savedTipType.name,
                description = savedTipType.displayName            )

            Result.success(dto)
        } catch (e: Exception) {
            when (e.message) {
                "Name cannot be empty" -> Result.failure("TipType_Error_NameInvalid")
                else -> Result.failure("TipType_Error_UpdateFailed: ${e.message}")
            }
        }
    }
}