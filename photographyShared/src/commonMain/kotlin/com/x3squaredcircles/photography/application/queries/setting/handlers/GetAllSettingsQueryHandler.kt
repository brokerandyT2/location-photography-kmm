// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/handlers/GetAllSettingsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.setting.handlers

import com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsQuery
import com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import co.touchlab.kermit.Logger

class GetAllSettingsQueryHandler(
    private val settingRepository: ISettingRepository,
    private val logger: Logger
) : IQueryHandler<GetAllSettingsQuery, GetAllSettingsQueryResult> {

    override suspend fun handle(query: GetAllSettingsQuery): GetAllSettingsQueryResult {
        return try {
            logger.d { "Handling GetAllSettingsQuery" }

            val settings = settingRepository.getAllAsync()

            logger.i { "Retrieved ${settings.size} settings" }

            GetAllSettingsQueryResult(
                settings = settings,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all settings" }
            GetAllSettingsQueryResult(
                settings = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}