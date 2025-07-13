// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/GetTipTypeByIdQueryHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.photography.queries.GetTipTypeByIdQuery
import com.x3squaredcircles.photography.dtos.TipTypeDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ITipTypeRepository

class GetTipTypeByIdQueryHandler(
    private val tipTypeRepository: ITipTypeRepository
) : IQueryHandler<GetTipTypeByIdQuery, Result<TipTypeDto>> {

    override suspend fun handle(request: GetTipTypeByIdQuery): Result<TipTypeDto> {
        return try {
            val result = tipTypeRepository.getByIdAsync(request.id)

            if (!result.isSuccess) {
                return Result.failure("TipType_Error_NotFoundById: ${request.id}")
            }

            val tipType = result.getOrNull()
            if (tipType == null) {
                Result.failure("TipType_Error_NotFoundById: ${request.id}")
            } else {
                val tipTypeDto = TipTypeDto(
                    id = tipType.id,
                    name = tipType.name,
                    description = tipType.displayName
                )
                Result.success(tipTypeDto)
            }
        } catch (ex: Exception) {
            Result.failure("TipType_Error_RetrieveFailed: ${ex.message}")
        }
    }
}