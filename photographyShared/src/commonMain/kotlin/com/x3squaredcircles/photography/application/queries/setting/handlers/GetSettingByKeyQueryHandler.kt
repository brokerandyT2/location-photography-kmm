// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/handlers/GetSettingByKeyQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.setting.handlers

import com.x3squaredcircles.photography.application.queries.setting.GetSettingByKeyQuery
import com.x3squaredcircles.photography.application.queries.setting.GetSettingByKeyQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetSettingByKeyQueryHandler(
    private val settingRepository: ISettingRepository,
    private val logger: Logger
) : IQueryHandler<GetSettingByKeyQuery, GetSettingByKeyQueryResult> {

    override suspend fun handle(query: GetSettingByKeyQuery): Result<GetSettingByKeyQueryResult> {
        logger.d { "Handling GetSettingByKeyQuery with key: ${query.key}" }

        return when (val result = settingRepository.getByKeyAsync(query.key)) {
            is Result.Success -> {
                logger.i { "Retrieved setting with key: ${query.key}, found: ${result.data != null}" }
                Result.success(
                    GetSettingByKeyQueryResult(
                        setting = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get setting by key: ${query.key} - ${result.error}" }
                Result.success(
                    GetSettingByKeyQueryResult(
                        setting = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}