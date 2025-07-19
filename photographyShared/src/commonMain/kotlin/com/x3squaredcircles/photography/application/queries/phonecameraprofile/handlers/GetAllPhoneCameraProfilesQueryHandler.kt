// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/handlers/GetAllPhoneCameraProfilesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile.handlers

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetAllPhoneCameraProfilesQuery
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetAllPhoneCameraProfilesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllPhoneCameraProfilesQueryHandler(
    private val phoneCameraProfileRepository: IPhoneCameraProfileRepository,
    private val logger: Logger
) : IQueryHandler<GetAllPhoneCameraProfilesQuery, GetAllPhoneCameraProfilesQueryResult> {

    override suspend fun handle(query: GetAllPhoneCameraProfilesQuery): Result<GetAllPhoneCameraProfilesQueryResult> {
        logger.d { "Handling GetAllPhoneCameraProfilesQuery" }

        return when (val result = phoneCameraProfileRepository.getAllAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} phone camera profiles" }
                Result.success(
                    GetAllPhoneCameraProfilesQueryResult(
                        profiles = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all phone camera profiles: ${result.error}" }
                Result.success(
                    GetAllPhoneCameraProfilesQueryResult(
                        profiles = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}