// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/handlers/GetSettingByKeyQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.setting.handlers

import com.x3squaredcircles.photography.application.queries.setting.GetSettingByKeyQuery
import com.x3squaredcircles.photography.application.queries.setting.GetSettingByKeyQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import co.touchlab.kermit.Logger

class GetSettingByKeyQueryHandler(
    private val settingRepository: ISettingRepository,
    private val logger: Logger
) : IQueryHandler<GetSettingByKeyQuery, GetSettingByKeyQueryResult> {

    override suspend fun handle(query: GetSettingByKeyQuery): GetSettingByKeyQueryResult {
        return try {
            logger.d { "Handling GetSettingByKeyQuery with key: ${query.key}" }

            val setting = settingRepository.getByKeyAsync(query.key)

            logger.i { "Retrieved setting with key: ${query.key}, found: ${setting != null}" }

            GetSettingByKeyQueryResult(
                setting = setting,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get setting by key: ${query.key}" }
            GetSettingByKeyQueryResult(
                setting = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}