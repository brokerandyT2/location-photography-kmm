// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipsWithCameraSettingsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetTipsWithCameraSettingsQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipsWithCameraSettingsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetTipsWithCameraSettingsQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipsWithCameraSettingsQuery, GetTipsWithCameraSettingsQueryResult> {

    override suspend fun handle(query: GetTipsWithCameraSettingsQuery): Result<GetTipsWithCameraSettingsQueryResult> {
        logger.d { "Handling GetTipsWithCameraSettingsQuery" }

        return when (val result = tipRepository.getWithCameraSettingsAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} tips with camera settings" }
                Result.success(
                    GetTipsWithCameraSettingsQueryResult(
                        tips = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get tips with camera settings: ${result.error}" }
                Result.success(
                    GetTipsWithCameraSettingsQueryResult(
                        tips = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}