// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/GetAllTipTypesQueryHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.photography.queries.GetAllTipTypesQuery
import com.x3squaredcircles.photography.dtos.TipTypeDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ITipTypeRepository

class GetAllTipTypesQueryHandler(
    private val tipTypeRepository: ITipTypeRepository
) : IQueryHandler<GetAllTipTypesQuery, Result<List<TipTypeDto>>> {

    override suspend fun handle(request: GetAllTipTypesQuery): Result<List<TipTypeDto>> {
        return try {
            val result = tipTypeRepository.getAllAsync()

            if (!result.isSuccess) {
                return Result.failure("TipType_Error_ListRetrieveFailed")
            }

            val tipTypes = result.getOrNull() ?: emptyList()

            if (tipTypes.isEmpty()) {
                Result.failure("TipType_Error_ListRetrieveFailed")
            } else {
                val tipTypeDtos = tipTypes.map { tipType ->
                    TipTypeDto(
                        id = tipType.id,
                        name = tipType.name,
                        description = tipType.displayName
                    )
                }

                Result.success(tipTypeDtos)
            }
        } catch (ex: Exception) {
            Result.failure("TipType_Error_ListRetrieveFailedWithException: ${ex.message}")
        }
    }
}