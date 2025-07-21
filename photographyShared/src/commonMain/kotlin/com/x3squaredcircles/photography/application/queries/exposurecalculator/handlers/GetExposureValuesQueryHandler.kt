// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/exposurecalculator/handlers/GetExposureValuesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.exposurecalculator.handlers

import com.x3squaredcircles.photography.application.queries.exposurecalculator.GetExposureValuesQuery
import com.x3squaredcircles.photography.application.queries.exposurecalculator.GetExposureValuesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.domain.services.IExposureCalculatorService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetExposureValuesQueryHandler(
    private val exposureCalculatorService: IExposureCalculatorService,
    private val logger: Logger
) : IQueryHandler<GetExposureValuesQuery, GetExposureValuesQueryResult> {

    override suspend fun handle(query: GetExposureValuesQuery): Result<GetExposureValuesQueryResult> {
        logger.d { "Handling GetExposureValuesQuery with increments: ${query.increments}" }

        return try {
            val shutterSpeedsResult = exposureCalculatorService.getShutterSpeedsAsync(query.increments)
            val aperturesResult = exposureCalculatorService.getAperturesAsync(query.increments)
            val isosResult = exposureCalculatorService.getIsosAsync(query.increments)

            when {
                shutterSpeedsResult.isFailure -> {
                    val errorMessage = (shutterSpeedsResult as Result.Failure).error
                    logger.e { "Failed to get shutter speeds: $errorMessage" }
                    Result.success(
                        GetExposureValuesQueryResult(
                            shutterSpeeds = emptyArray(),
                            apertures = emptyArray(),
                            isos = emptyArray(),
                            isSuccess = false,
                            errorMessage = errorMessage
                        )
                    )
                }
                aperturesResult.isFailure -> {
                    val errorMessage = (aperturesResult as Result.Failure).error
                    logger.e { "Failed to get apertures: $errorMessage" }
                    Result.success(
                        GetExposureValuesQueryResult(
                            shutterSpeeds = emptyArray(),
                            apertures = emptyArray(),
                            isos = emptyArray(),
                            isSuccess = false,
                            errorMessage = errorMessage
                        )
                    )
                }
                isosResult.isFailure -> {
                    val errorMessage = (isosResult as Result.Failure).error
                    logger.e { "Failed to get ISOs: $errorMessage" }
                    Result.success(
                        GetExposureValuesQueryResult(
                            shutterSpeeds = emptyArray(),
                            apertures = emptyArray(),
                            isos = emptyArray(),
                            isSuccess = false,
                            errorMessage = errorMessage
                        )
                    )
                }
                else -> {
                    val shutterSpeeds = (shutterSpeedsResult as Result.Success).data
                    val apertures = (aperturesResult as Result.Success).data
                    val isos = (isosResult as Result.Success).data

                    logger.i { "Successfully retrieved exposure values: ${shutterSpeeds.size} shutter speeds, ${apertures.size} apertures, ${isos.size} ISOs" }
                    Result.success(
                        GetExposureValuesQueryResult(
                            shutterSpeeds = shutterSpeeds,
                            apertures = apertures,
                            isos = isos,
                            isSuccess = true
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error retrieving exposure values" }
            Result.success(
                GetExposureValuesQueryResult(
                    shutterSpeeds = emptyArray(),
                    apertures = emptyArray(),
                    isos = emptyArray(),
                    isSuccess = false,
                    errorMessage = "Error retrieving exposure values: ${ex.message}"
                )
            )
        }
    }
}