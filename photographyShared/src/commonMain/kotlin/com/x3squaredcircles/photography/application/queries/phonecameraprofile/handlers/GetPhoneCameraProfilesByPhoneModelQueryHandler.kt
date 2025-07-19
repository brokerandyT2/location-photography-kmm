// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/handlers/GetPhoneCameraProfilesByPhoneModelQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile.handlers

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesByPhoneModelQuery
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesByPhoneModelQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetPhoneCameraProfilesByPhoneModelQueryHandler(
    private val phoneCameraProfileRepository: IPhoneCameraProfileRepository,
    private val logger: Logger
) : IQueryHandler<GetPhoneCameraProfilesByPhoneModelQuery, GetPhoneCameraProfilesByPhoneModelQueryResult> {

    override suspend fun handle(query: GetPhoneCameraProfilesByPhoneModelQuery): Result<GetPhoneCameraProfilesByPhoneModelQueryResult> {
        logger.d { "Handling GetPhoneCameraProfilesByPhoneModelQuery with phoneModel: ${query.phoneModel}" }

        return when (val result = phoneCameraProfileRepository.getByPhoneModelAsync(query.phoneModel)) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} phone camera profiles for phone model: ${query.phoneModel}" }
                Result.success(
                    GetPhoneCameraProfilesByPhoneModelQueryResult(
                        profiles = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get phone camera profiles by phone model: ${query.phoneModel} - ${result.error}" }
                Result.success(
                    GetPhoneCameraProfilesByPhoneModelQueryResult(
                        profiles = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}