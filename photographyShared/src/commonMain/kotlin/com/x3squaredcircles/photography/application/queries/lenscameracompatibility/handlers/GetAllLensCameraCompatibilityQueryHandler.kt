// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/handlers/GetAllLensCameraCompatibilityQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility.handlers

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetAllLensCameraCompatibilityQuery
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetAllLensCameraCompatibilityQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import co.touchlab.kermit.Logger

class GetAllLensCameraCompatibilityQueryHandler(
    private val lensCameraCompatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : IQueryHandler<GetAllLensCameraCompatibilityQuery, GetAllLensCameraCompatibilityQueryResult> {

    override suspend fun handle(query: GetAllLensCameraCompatibilityQuery): GetAllLensCameraCompatibilityQueryResult {
        return try {
            logger.d { "Handling GetAllLensCameraCompatibilityQuery" }

            val compatibilities = lensCameraCompatibilityRepository.getAllAsync()

            logger.i { "Retrieved ${compatibilities.size} lens camera compatibilities" }

            GetAllLensCameraCompatibilityQueryResult(
                compatibilities = compatibilities,
                isSuccess = true
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get all lens camera compatibilities" }
            GetAllLensCameraCompatibilityQueryResult(
                compatibilities = emptyList(),
                isSuccess = false,
                errorMessage = ex.message
            )
        }
    }
}