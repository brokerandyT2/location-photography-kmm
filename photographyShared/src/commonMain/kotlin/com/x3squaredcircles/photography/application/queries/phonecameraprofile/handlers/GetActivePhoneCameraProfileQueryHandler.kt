// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/handlers/GetActivePhoneCameraProfileQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile.handlers

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetActivePhoneCameraProfileQuery
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetActivePhoneCameraProfileQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetActivePhoneCameraProfileQueryHandler(
    private val phoneCameraProfileRepository: IPhoneCameraProfileRepository,
    private val logger: Logger
) : IQueryHandler<GetActivePhoneCameraProfileQuery, GetActivePhoneCameraProfileQueryResult> {

    override suspend fun handle(query: GetActivePhoneCameraProfileQuery): Result<GetActivePhoneCameraProfileQueryResult> {
        logger.d { "Handling GetActivePhoneCameraProfileQuery" }

        return when (val result = phoneCameraProfileRepository.getActiveAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved active phone camera profile: ${result.data != null}" }
                Result.success(
                    GetActivePhoneCameraProfileQueryResult(
                        profile = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get active phone camera profile: ${result.error}" }
                Result.success(
                    GetActivePhoneCameraProfileQueryResult(
                        profile = null,
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}