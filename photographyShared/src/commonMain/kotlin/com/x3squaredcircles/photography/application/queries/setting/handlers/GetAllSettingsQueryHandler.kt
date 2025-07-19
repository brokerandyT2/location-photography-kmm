// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/handlers/GetAllSettingsQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.setting.handlers

import com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsQuery
import com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllSettingsQueryHandler(
    private val settingRepository: ISettingRepository,
    private val logger: Logger
) : IQueryHandler<GetAllSettingsQuery, GetAllSettingsQueryResult> {

    override suspend fun handle(query: GetAllSettingsQuery): Result<GetAllSettingsQueryResult> {
        logger.d { "Handling GetAllSettingsQuery" }

        return when (val result = settingRepository.getAllAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} settings" }
                Result.success(
                    GetAllSettingsQueryResult(
                        settings = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all settings: ${result.error}" }
                Result.success(
                    GetAllSettingsQueryResult(
                        settings = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}