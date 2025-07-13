// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/CreateTipCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.CreateTipCommand
import com.x3squaredcircles.photography.domain.entities.Tip
import com.x3squaredcircles.photography.dtos.TipDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ITipRepository
import kotlinx.datetime.Clock
class CreateTipCommandHandler(
    private val tipRepository: ITipRepository
) : ICommandHandler<CreateTipCommand, Result<TipDto>> {
    override suspend fun handle(request: CreateTipCommand): Result<TipDto> {
        return try {
            val currentTime = Clock.System.now().epochSeconds
            val tip = Tip(
                id = 0,
                tipTypeId = request.tipTypeId,
                title = request.title,
                content = request.content,
                fstop = request.fstop,
                shutterSpeed = request.shutterSpeed,
                iso = request.iso,
                dateAdded = currentTime,
                isUserCreated = request.isUserCreated
            )

            val createResult = tipRepository.createAsync(tip)
            if (!createResult.isSuccess) {
                return Result.failure("Error creating tip")
            }

            val createdTip = createResult.getOrNull()!!
            val tipDto = TipDto(
                id = createdTip.id,
                tipTypeId = createdTip.tipTypeId,
                title = createdTip.title,
                content = createdTip.content,
                fstop = createdTip.fstop,
                shutterSpeed = createdTip.shutterSpeed,
                iso = createdTip.iso,
                dateAdded = createdTip.dateAdded,
                isUserCreated = createdTip.isUserCreated
            )

            Result.success(tipDto)
        } catch (e: Exception) {
            when (e.message) {
                "Title cannot be empty" -> Result.failure("Tip_ValidationError_TitleRequired")
                "Content cannot be empty" -> Result.failure("Tip_ValidationError_ContentRequired")
                "Invalid tip type" -> Result.failure("Tip_Error_InvalidTipType")
                else -> Result.failure("Tip_Error_CreateFailed: ${e.message}")
            }
        }
    }
}