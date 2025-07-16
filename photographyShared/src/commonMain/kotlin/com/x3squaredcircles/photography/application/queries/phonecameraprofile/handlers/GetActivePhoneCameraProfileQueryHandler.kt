// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/handlers/GetActivePhoneCameraProfileQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile.handlers

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetActivePhoneCameraProfileQuery
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetActivePhoneCameraProfileQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import co.touchlab.kermit.Logger

class GetActivePhoneCameraProfileQueryHandler(
    private val phoneCameraProfileRepository: IPhoneCameraProfileRepository,
    private val logger: Logger
) : IQueryHandler<GetActivePhoneCameraProfileQuery, GetActivePhoneCameraProfileQueryResult> {

    override suspend fun handle(query: GetActivePhoneCameraProfileQuery): GetActivePhoneCameraProfileQueryResult {
        return try {
            logger.d { "Handling GetActivePhoneCameraProfileQuery" }

            val profile = phoneCameraProfileRepository.getActiveAsync()

            logger.i { "Retrieved active phone camera profile: ${profile != null}" }

            GetActivePhoneCameraProfileQueryResult(
                profile = profile,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get active phone camera profile" }
            GetActivePhoneCameraProfileQueryResult(
                profile = null,
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}