// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/handlers/GetPhoneCameraProfilesByPhoneModelQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile.handlers

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesByPhoneModelQuery
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesByPhoneModelQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import co.touchlab.kermit.Logger

class GetPhoneCameraProfilesByPhoneModelQueryHandler(
    private val phoneCameraProfileRepository: IPhoneCameraProfileRepository,
    private val logger: Logger
) : IQueryHandler<GetPhoneCameraProfilesByPhoneModelQuery, GetPhoneCameraProfilesByPhoneModelQueryResult> {

    override suspend fun handle(query: GetPhoneCameraProfilesByPhoneModelQuery): GetPhoneCameraProfilesByPhoneModelQueryResult {
        return try {
            logger.d { "Handling GetPhoneCameraProfilesByPhoneModelQuery with phoneModel: ${query.phoneModel}" }

            val profiles = phoneCameraProfileRepository.getByPhoneModelAsync(query.phoneModel)

            logger.i { "Retrieved ${profiles.size} phone camera profiles for phone model: ${query.phoneModel}" }

            GetPhoneCameraProfilesByPhoneModelQueryResult(
                profiles = profiles,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get phone camera profiles by phone model: ${query.phoneModel}" }
            GetPhoneCameraProfilesByPhoneModelQueryResult(
                profiles = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}