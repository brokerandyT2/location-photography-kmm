// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/handlers/GetAllPhoneCameraProfilesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile.handlers

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetAllPhoneCameraProfilesQuery
import com.x3squaredcircles.photography.application.queries.phonecameraprofile.GetAllPhoneCameraProfilesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import co.touchlab.kermit.Logger

class GetAllPhoneCameraProfilesQueryHandler(
    private val phoneCameraProfileRepository: IPhoneCameraProfileRepository,
    private val logger: Logger
) : IQueryHandler<GetAllPhoneCameraProfilesQuery, GetAllPhoneCameraProfilesQueryResult> {

    override suspend fun handle(query: GetAllPhoneCameraProfilesQuery): GetAllPhoneCameraProfilesQueryResult {
        return try {
            logger.d { "Handling GetAllPhoneCameraProfilesQuery" }

            val profiles = phoneCameraProfileRepository.getAllAsync()

            logger.i { "Retrieved ${profiles.size} phone camera profiles" }

            GetAllPhoneCameraProfilesQueryResult(
                profiles = profiles,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all phone camera profiles" }
            GetAllPhoneCameraProfilesQueryResult(
                profiles = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}