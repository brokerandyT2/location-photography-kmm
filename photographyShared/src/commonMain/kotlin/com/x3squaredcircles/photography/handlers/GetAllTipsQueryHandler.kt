// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/GetAllTipsQueryHandler.kt
package com.x3squaredcircles.photographyshared.handlers

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.photography.queries.GetAllTipsQuery
import com.x3squaredcircles.photography.dtos.TipDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ITipRepository

class GetAllTipsQueryHandler(
    private val tipRepository: ITipRepository
) : IQueryHandler<GetAllTipsQuery, Result<List<TipDto>>> {

    override suspend fun handle(request: GetAllTipsQuery): Result<List<TipDto>> {
        return try {
            val result = tipRepository.getAllAsync()

            if (!result.isSuccess) {
                return Result.failure("Tip_Error_RetrieveFailed")
            }

            val tips = result.getOrNull() ?: emptyList()
            val tipDtos = tips.map { tip ->
                TipDto(
                    id = tip.id,
                    tipTypeId = tip.tipTypeId,
                    title = tip.title,
                    content = tip.content,
                    fstop = tip.fstop,
                    shutterSpeed = tip.shutterSpeed,
                    iso = tip.iso

                )
            }

            Result.success(tipDtos)
        } catch (ex: Exception) {
            Result.failure("Tip_Error_RetrieveFailed: ${ex.message}")
        }
    }
}