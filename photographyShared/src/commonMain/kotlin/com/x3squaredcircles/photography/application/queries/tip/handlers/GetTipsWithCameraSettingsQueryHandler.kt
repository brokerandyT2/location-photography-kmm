// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/handlers/GetTipsWithCameraSettingsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.tip.handlers

import com.x3squaredcircles.photography.application.queries.tip.GetTipsWithCameraSettingsQuery
import com.x3squaredcircles.photography.application.queries.tip.GetTipsWithCameraSettingsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import co.touchlab.kermit.Logger

class GetTipsWithCameraSettingsQueryHandler(
    private val tipRepository: ITipRepository,
    private val logger: Logger
) : IQueryHandler<GetTipsWithCameraSettingsQuery, GetTipsWithCameraSettingsQueryResult> {

    override suspend fun handle(query: GetTipsWithCameraSettingsQuery): GetTipsWithCameraSettingsQueryResult {
        return try {
            logger.d { "Handling GetTipsWithCameraSettingsQuery" }

            val tips = tipRepository.getWithCameraSettingsAsync()

            logger.i { "Retrieved ${tips.size} tips with camera settings" }

            GetTipsWithCameraSettingsQueryResult(
                tips = tips,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get tips with camera settings" }
            GetTipsWithCameraSettingsQueryResult(
                tips = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}