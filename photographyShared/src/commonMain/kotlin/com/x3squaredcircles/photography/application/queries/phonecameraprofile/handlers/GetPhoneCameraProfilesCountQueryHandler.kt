// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/handlers/GetPhoneCameraProfilesCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile.handlers

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesCountQuery
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetPhoneCameraProfilesCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import co.touchlab.kermit.Logger

class GetPhoneCameraProfilesCountQueryHandler(
    private val phoneCameraProfileRepository: IPhoneCameraProfileRepository,
    private val logger: Logger
) : IQueryHandler<GetPhoneCameraProfilesCountQuery, GetPhoneCameraProfilesCountQueryResult> {

    override suspend fun handle(query: GetPhoneCameraProfilesCountQuery): GetPhoneCameraProfilesCountQueryResult {
        return try {
            logger.d { "Handling GetPhoneCameraProfilesCountQuery" }

            val count = phoneCameraProfileRepository.getTotalCountAsync()

            logger.i { "Retrieved total phone camera profiles count: $count" }

            GetPhoneCameraProfilesCountQueryResult(
                count = count,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get phone camera profiles count" }
            GetPhoneCameraProfilesCountQueryResult(
                count = 0L,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}