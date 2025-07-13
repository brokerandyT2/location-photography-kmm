// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/CreateTipTypeCommandHandler.kt
package com.x3squaredcircles.photographyshared.handlers
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.ICommandHandler
import com.x3squaredcircles.photography.commands.CreateTipTypeCommand
import com.x3squaredcircles.photography.domain.entities.TipType
import com.x3squaredcircles.photography.dtos.TipTypeDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ITipTypeRepository
import kotlinx.datetime.Clock
class CreateTipTypeCommandHandler(
    private val tipTypeRepository: ITipTypeRepository
) : ICommandHandler<CreateTipTypeCommand, Result<TipTypeDto>> {
    override suspend fun handle(request: CreateTipTypeCommand): Result<TipTypeDto> {
        return try {
            val existsByNameResult = tipTypeRepository.existsByNameAsync(request.name)
            if (existsByNameResult.isSuccess && existsByNameResult.getOrNull() == true) {
                return Result.failure("TipType with this name already exists")
            }

            val currentTime = Clock.System.now().epochSeconds
            val tipType = TipType(
                id = 0,
                name = request.name
            )

            val createResult = tipTypeRepository.createAsync(tipType)
            if (!createResult.isSuccess) {
                return Result.failure("Error creating tip type")
            }

            val createdTipType = createResult.getOrNull()!!
            val tipTypeDto = TipTypeDto(
                id = createdTipType.id,
                name = createdTipType.name,
                description = createdTipType.displayName
            )

            Result.success(tipTypeDto)
        } catch (e: Exception) {
            when (e.message) {
                "Name cannot be empty" -> Result.failure("TipType_Error_NameInvalid")
                else -> Result.failure("TipType_Error_CreateFailed: ${e.message}")
            }
        }
    }
}