// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/handlers/GetActivePhoneCameraProfilesCountQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile.handlers

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetActivePhoneCameraProfilesCountQuery
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetActivePhoneCameraProfilesCountQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import co.touchlab.kermit.Logger

class GetActivePhoneCameraProfilesCountQueryHandler(
    private val phoneCameraProfileRepository: IPhoneCameraProfileRepository,
    private val logger: Logger
) : IQueryHandler<GetActivePhoneCameraProfilesCountQuery, GetActivePhoneCameraProfilesCountQueryResult> {

    override suspend fun handle(query: GetActivePhoneCameraProfilesCountQuery): GetActivePhoneCameraProfilesCountQueryResult {
        return try {
            logger.d { "Handling GetActivePhoneCameraProfilesCountQuery" }

            val count = phoneCameraProfileRepository.getActiveCountAsync()

            logger.i { "Retrieved active phone camera profiles count: $count" }

            GetActivePhoneCameraProfilesCountQueryResult(
                count = count,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get active phone camera profiles count" }
            GetActivePhoneCameraProfilesCountQueryResult(
                count = 0L,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}