// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/handlers/GetActivePhoneCameraProfilesCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile.handlers

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetActivePhoneCameraProfilesCountQuery
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetActivePhoneCameraProfilesCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetActivePhoneCameraProfilesCountQueryHandler(
    private val phoneCameraProfileRepository: IPhoneCameraProfileRepository,
    private val logger: Logger
) : IQueryHandler<GetActivePhoneCameraProfilesCountQuery, GetActivePhoneCameraProfilesCountQueryResult> {

    override suspend fun handle(query: GetActivePhoneCameraProfilesCountQuery): Result<GetActivePhoneCameraProfilesCountQueryResult> {
        logger.d { "Handling GetActivePhoneCameraProfilesCountQuery" }

        return when (val result = phoneCameraProfileRepository.getActiveCountAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved active phone camera profiles count: ${result.data}" }
                Result.success(
                    GetActivePhoneCameraProfilesCountQueryResult(
                        count = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get active phone camera profiles count: ${result.error}" }
                Result.success(
                    GetActivePhoneCameraProfilesCountQueryResult(
                        count = 0L,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}