// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/handlers/GetSettingsByKeysQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.setting.handlers

import com.x3squaredcircles.photography.application.queries.setting.GetSettingsByKeysQuery
import com.x3squaredcircles.photography.application.queries.setting.GetSettingsByKeysQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import co.touchlab.kermit.Logger

class GetSettingsByKeysQueryHandler(
    private val settingRepository: ISettingRepository,
    private val logger: Logger
) : IQueryHandler<GetSettingsByKeysQuery, GetSettingsByKeysQueryResult> {

    override suspend fun handle(query: GetSettingsByKeysQuery): GetSettingsByKeysQueryResult {
        return try {
            logger.d { "Handling GetSettingsByKeysQuery with ${query.keys.size} keys" }

            val settings = settingRepository.getByKeysAsync(query.keys)

            logger.i { "Retrieved ${settings.size} settings for ${query.keys.size} keys" }

            GetSettingsByKeysQueryResult(
                settings = settings,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get settings by keys" }
            GetSettingsByKeysQueryResult(
                settings = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}