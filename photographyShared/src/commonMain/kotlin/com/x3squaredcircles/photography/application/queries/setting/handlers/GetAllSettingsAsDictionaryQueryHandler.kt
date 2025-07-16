// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/handlers/GetAllSettingsAsDictionaryQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.setting.handlers

import com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsAsDictionaryQuery
import com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsAsDictionaryQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import co.touchlab.kermit.Logger

class GetAllSettingsAsDictionaryQueryHandler(
    private val settingRepository: ISettingRepository,
    private val logger: Logger
) : IQueryHandler<GetAllSettingsAsDictionaryQuery, GetAllSettingsAsDictionaryQueryResult> {

    override suspend fun handle(query: GetAllSettingsAsDictionaryQuery): GetAllSettingsAsDictionaryQueryResult {
        return try {
            logger.d { "Handling GetAllSettingsAsDictionaryQuery" }

            val settings = settingRepository.getAllAsDictionaryAsync()

            logger.i { "Retrieved ${settings.size} settings as dictionary" }

            GetAllSettingsAsDictionaryQueryResult(
                settings = settings,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all settings as dictionary" }
            GetAllSettingsAsDictionaryQueryResult(
                settings = emptyMap(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}