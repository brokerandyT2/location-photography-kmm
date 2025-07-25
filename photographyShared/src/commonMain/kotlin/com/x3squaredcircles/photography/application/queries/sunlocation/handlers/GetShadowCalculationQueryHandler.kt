package com.x3squaredcircles.photography.application.queries.sunlocation.handlers

import com.x3squaredcircles.photography.application.queries.sunlocation.GetShadowCalculationQuery
import com.x3squaredcircles.photography.application.queries.sunlocation.GetShadowCalculationQueryResult
import com.x3squaredcircles.photography.application.queries.sunlocation.ShadowCalculationResultDto
import com.x3squaredcircles.photography.application.queries.sunlocation.ShadowTimePointDto
import com.x3squaredcircles.photography.application.queries.sunlocation.TerrainType
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.domain.services.ISunCalculatorService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import kotlinx.datetime.TimeZone
import kotlin.time.Duration.Companion.hours
import kotlin.math.PI
import kotlin.math.tan

class GetShadowCalculationQueryHandler(
    private val sunCalculatorService: ISunCalculatorService,
    private val logger: Logger
) : IQueryHandler<GetShadowCalculationQuery, GetShadowCalculationQueryResult> {

    override suspend fun handle(query: GetShadowCalculationQuery): Result<GetShadowCalculationQueryResult> {
        logger.d { "Handling GetShadowCalculationQuery for coordinates: ${query.latitude}, ${query.longitude} at ${query.dateTime}" }

        return try {
            val timezone = TimeZone.currentSystemDefault().toString()

            val sunElevation = sunCalculatorService.getSolarElevation(query.dateTime, query.latitude, query.longitude, timezone)
            val sunAzimuth = sunCalculatorService.getSolarAzimuth(query.dateTime, query.latitude, query.longitude, timezone)

            val shadowLength = if (sunElevation > 0) {
                query.objectHeight / tan(sunElevation * PI / 180.0)
            } else {
                Double.MAX_VALUE
            }

            val shadowDirection = (sunAzimuth + 180) % 360

            val terrainMultiplier = getTerrainMultiplier(query.terrainType)
            val adjustedShadowLength = shadowLength * terrainMultiplier

            val shadowProgression = mutableListOf<ShadowTimePointDto>()
            val startTime = query.dateTime.plus((-12).hours)

            for (i in 0..24) {
                val time = startTime.plus(i.hours)
                val elevation = sunCalculatorService.getSolarElevation(time, query.latitude, query.longitude, timezone)
                val azimuth = sunCalculatorService.getSolarAzimuth(time, query.latitude, query.longitude, timezone)

                if (elevation > 0) {
                    val length = query.objectHeight / tan(elevation * PI / 180.0) * terrainMultiplier
                    val direction = (azimuth + 180) % 360

                    shadowProgression.add(
                        ShadowTimePointDto(
                            time = time,
                            length = length,
                            direction = direction
                        )
                    )
                }
            }

            val result = ShadowCalculationResultDto(
                shadowLength = adjustedShadowLength,
                shadowDirection = shadowDirection,
                objectHeight = query.objectHeight,
                calculationTime = query.dateTime,
                terrain = query.terrainType,
                shadowProgression = shadowProgression
            )

            logger.i { "Calculated shadow data - length: $adjustedShadowLength, direction: $shadowDirection" }
            Result.success(
                GetShadowCalculationQueryResult(
                    shadowCalculation = result,
                    isSuccess = true
                )
            )
        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating shadow data for coordinates ${query.latitude}, ${query.longitude} at ${query.dateTime}" }
            Result.success(
                GetShadowCalculationQueryResult(
                    shadowCalculation = ShadowCalculationResultDto(
                        shadowLength = 0.0,
                        shadowDirection = 0.0,
                        objectHeight = query.objectHeight,
                        calculationTime = query.dateTime,
                        terrain = query.terrainType,
                        shadowProgression = emptyList()
                    ),
                    isSuccess = false,
                    errorMessage = "Error calculating shadows: ${ex.message}"
                )
            )
        }
    }

    private fun getTerrainMultiplier(terrain: TerrainType): Double {
        return when (terrain) {
            TerrainType.FLAT -> 1.0
            TerrainType.URBAN -> 0.8
            TerrainType.FOREST -> 0.6
            TerrainType.MOUNTAIN -> 1.2
            TerrainType.BEACH -> 1.1
        }
    }
}