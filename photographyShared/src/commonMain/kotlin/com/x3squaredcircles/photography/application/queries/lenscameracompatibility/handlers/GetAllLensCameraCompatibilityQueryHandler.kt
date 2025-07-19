// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lenscameracompatibility/handlers/GetAllLensCameraCompatibilityQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lenscameracompatibility.handlers

import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetAllLensCameraCompatibilityQuery
import com.x3squaredcircles.photography.application.queries.lenscameracompatibility.GetAllLensCameraCompatibilityQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensCameraCompatibilityRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetAllLensCameraCompatibilityQueryHandler(
    private val lensCameraCompatibilityRepository: ILensCameraCompatibilityRepository,
    private val logger: Logger
) : IQueryHandler<GetAllLensCameraCompatibilityQuery, GetAllLensCameraCompatibilityQueryResult> {

    override suspend fun handle(query: GetAllLensCameraCompatibilityQuery): Result<GetAllLensCameraCompatibilityQueryResult> {
        logger.d { "Handling GetAllLensCameraCompatibilityQuery" }

        return when (val result = lensCameraCompatibilityRepository.getAllAsync()) {
            is Result.Success -> {
                logger.i { "Retrieved ${result.data.size} lens camera compatibilities" }
                Result.success(
                    GetAllLensCameraCompatibilityQueryResult(
                        compatibilities = result.data,
                        isSuccess = true
                    )
                )
            }
            is Result.Failure -> {
                logger.e { "Failed to get all lens camera compatibilities: ${result.error}" }
                Result.success(
                    GetAllLensCameraCompatibilityQueryResult(
                        compatibilities = emptyList(),
                        isSuccess = false,
                        errorMessage = result.error
                    )
                )
            }
        }
    }
}