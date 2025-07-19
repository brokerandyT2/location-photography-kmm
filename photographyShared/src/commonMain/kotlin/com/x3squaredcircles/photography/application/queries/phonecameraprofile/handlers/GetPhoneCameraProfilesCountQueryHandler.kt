// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/handlers/GetPhoneCameraProfilesCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile.handlers

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesCountQuery
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetPhoneCameraProfilesCountQueryHandler(
    private val phoneCameraProfileRepository: IPhoneCameraProfileRepository,
    private val logger: Logger
) : IQueryHandler<GetPhoneCameraProfilesCountQuery, GetPhoneCameraProfilesCountQueryResult> {

    override suspend fun handle(query: GetPhoneCameraProfilesCountQuery): Result<GetPhoneCameraProfilesCountQueryResult> {
        logger.d { "Handling GetPhoneCameraProfilesCountQuery" }

        return when (val result = phoneCameraProfileRepository.getTotalCountAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved total phone camera profiles count: ${result.data}" }
                Result.success(
                    GetPhoneCameraProfilesCountQueryResult(
                        count = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get phone camera profiles count: ${result.error}" }
                Result.success(
                    GetPhoneCameraProfilesCountQueryResult(
                        count = 0L,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}