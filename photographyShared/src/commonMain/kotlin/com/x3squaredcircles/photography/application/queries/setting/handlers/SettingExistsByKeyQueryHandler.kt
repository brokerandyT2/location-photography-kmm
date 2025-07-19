// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/handlers/SettingExistsByKeyQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.setting.handlers

import com.x3squaredcircles.photography.application.queries.setting.SettingExistsByKeyQuery
import com.x3squaredcircles.photography.application.queries.setting.SettingExistsByKeyQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class SettingExistsByKeyQueryHandler(
    private val settingRepository: ISettingRepository,
    private val logger: Logger
) : IQueryHandler<SettingExistsByKeyQuery, SettingExistsByKeyQueryResult> {

    override suspend fun handle(query: SettingExistsByKeyQuery): Result<SettingExistsByKeyQueryResult> {
        logger.d { "Handling SettingExistsByKeyQuery with key: ${query.key}" }

        return when (val result = settingRepository.existsAsync(query.key)) {
            is Result.Success -> {
                logger.i { "Setting exists check for key '${query.key}': ${result.data}" }
                Result.success(
                    SettingExistsByKeyQueryResult(
                        exists = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to check if setting exists by key: ${query.key} - ${result.error}" }
                Result.success(
                    SettingExistsByKeyQueryResult(
                        exists = false,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}