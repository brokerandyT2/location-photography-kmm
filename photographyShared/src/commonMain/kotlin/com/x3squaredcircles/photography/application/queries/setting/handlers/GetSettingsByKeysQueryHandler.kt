// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/handlers/GetSettingsByKeysQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.setting.handlers

import com.x3squaredcircles.photography.application.queries.setting.GetSettingsByKeysQuery
import com.x3squaredcircles.photography.application.queries.setting.GetSettingsByKeysQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetSettingsByKeysQueryHandler(
    private val settingRepository: ISettingRepository,
    private val logger: Logger
) : IQueryHandler<GetSettingsByKeysQuery, GetSettingsByKeysQueryResult> {

    override suspend fun handle(query: GetSettingsByKeysQuery): Result<GetSettingsByKeysQueryResult> {
        logger.d { "Handling GetSettingsByKeysQuery with ${query.keys.size} keys" }

        return when (val result = settingRepository.getByKeysAsync(query.keys)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} settings for ${query.keys.size} keys" }
                Result.success(
                    GetSettingsByKeysQueryResult(
                        settings = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get settings by keys: ${result.error}" }
                Result.success(
                    GetSettingsByKeysQueryResult(
                        settings = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}