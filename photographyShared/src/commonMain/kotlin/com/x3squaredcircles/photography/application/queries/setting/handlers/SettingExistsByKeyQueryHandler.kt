// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/handlers/SettingExistsByKeyQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.setting.handlers

import com.x3squaredcircles.photography.application.queries.setting.SettingExistsByKeyQuery
import com.x3squaredcircles.photography.application.queries.setting.SettingExistsByKeyQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import co.touchlab.kermit.Logger

class SettingExistsByKeyQueryHandler(
    private val settingRepository: ISettingRepository,
    private val logger: Logger
) : IQueryHandler<SettingExistsByKeyQuery, SettingExistsByKeyQueryResult> {

    override suspend fun handle(query: SettingExistsByKeyQuery): SettingExistsByKeyQueryResult {
        return try {
            logger.d { "Handling SettingExistsByKeyQuery with key: ${query.key}" }

            val exists = settingRepository.existsAsync(query.key)

            logger.i { "Setting exists check for key '${query.key}': $exists" }

            SettingExistsByKeyQueryResult(
                exists = exists,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to check if setting exists by key: ${query.key}" }
            SettingExistsByKeyQueryResult(
                exists = false,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}