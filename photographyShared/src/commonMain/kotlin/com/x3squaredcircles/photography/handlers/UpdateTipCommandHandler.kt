// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/UpdateTipCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.UpdateTipCommand
import com.x3squaredcircles.photography.dtos.TipDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ITipRepository

class UpdateTipCommandHandler(
    private val tipRepository: ITipRepository
) : ICommandHandler<UpdateTipCommand, Result<TipDto>> {

    override suspend fun handle(request: UpdateTipCommand): Result<TipDto> {
        return try {
            val existingResult = tipRepository.getByIdAsync(request.id)
            if (!existingResult.isSuccess) {
                return Result.failure("Tip not found")
            }

            val existingTip = existingResult.getOrNull()
            if (existingTip == null) {
                return Result.failure("Tip not found")
            }

            val updatedTip = existingTip.copy(
                tipTypeId = request.tipTypeId,
                title = request.title,
                content = request.content,
                fstop = request.fstop,
                shutterSpeed = request.shutterSpeed,
                iso = request.iso
            )

            val updateResult = tipRepository.updateAsync(updatedTip)
            if (!updateResult.isSuccess) {
                return Result.failure("Error updating tip")
            }

            val savedTip = updateResult.getOrNull()!!
            val dto = TipDto(
                id = savedTip.id,
                tipTypeId = savedTip.tipTypeId,
                title = savedTip.title,
                content = savedTip.content,
                fstop = savedTip.fstop,
                shutterSpeed = savedTip.shutterSpeed,
                iso = savedTip.iso
            )

            Result.success(dto)
        } catch (e: Exception) {
            when (e.message) {
                "Title cannot be empty" -> Result.failure("Tip_ValidationError_TitleRequired")
                "Content cannot be empty" -> Result.failure("Tip_ValidationError_ContentRequired")
                else -> Result.failure("Tip_Error_UpdateFailed: ${e.message}")
            }
        }
    }
}