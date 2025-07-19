// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/handlers/GetAllSettingsAsDictionaryQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.setting.handlers

import com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsAsDictionaryQuery
import com.x3squaredcircles.photography.application.queries.setting.GetAllSettingsAsDictionaryQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllSettingsAsDictionaryQueryHandler(
    private val settingRepository: ISettingRepository,
    private val logger: Logger
) : IQueryHandler<GetAllSettingsAsDictionaryQuery, GetAllSettingsAsDictionaryQueryResult> {

    override suspend fun handle(query: GetAllSettingsAsDictionaryQuery): Result<GetAllSettingsAsDictionaryQueryResult> {
        logger.d { "Handling GetAllSettingsAsDictionaryQuery" }

        return when (val result = settingRepository.getAllAsDictionaryAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} settings as dictionary" }
                Result.success(
                    GetAllSettingsAsDictionaryQueryResult(
                        settings = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all settings as dictionary: ${result.error}" }
                Result.success(
                    GetAllSettingsAsDictionaryQueryResult(
                        settings = emptyMap(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}