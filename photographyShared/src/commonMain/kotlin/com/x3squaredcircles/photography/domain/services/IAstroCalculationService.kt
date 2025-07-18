// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/IAstroCalculationService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.enums.ConstellationType
import com.x3squaredcircles.photography.domain.enums.CoordinateType
import com.x3squaredcircles.photography.domain.enums.PlanetType
import com.x3squaredcircles.photography.domain.models.AtmosphericCorrectionData
import com.x3squaredcircles.photography.domain.models.ConstellationData
import com.x3squaredcircles.photography.domain.models.CoordinateTransformResult
import com.x3squaredcircles.photography.domain.models.DeepSkyObjectData
import com.x3squaredcircles.photography.domain.models.EnhancedMoonData
import com.x3squaredcircles.photography.domain.models.PlanetPositionData
import com.x3squaredcircles.photography.domain.models.PlanetaryConjunction
import com.x3squaredcircles.photography.domain.models.PlanetaryEvent
import kotlinx.datetime.Instant

interface IAstroCalculationService {

    suspend fun getPlanetPositionAsync(
        planet: PlanetType,
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): Result<PlanetPositionData>

    suspend fun getVisiblePlanetsAsync(
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): Result<List<PlanetPositionData>>

    suspend fun getPlanetaryConjunctionsAsync(
        startDate: Instant,
        endDate: Instant,
        latitude: Double,
        longitude: Double
    ): Result<List<PlanetaryConjunction>>

    suspend fun getPlanetOppositionsAsync(
        startDate: Instant,
        endDate: Instant
    ): Result<List<PlanetaryEvent>>

    suspend fun getEnhancedMoonDataAsync(
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): Result<EnhancedMoonData>

    suspend fun getConstellationDataAsync(
        constellation: ConstellationType,
        date: Instant,
        latitude: Double,
        longitude: Double
    ): Result<ConstellationData>

    suspend fun getDeepSkyObjectDataAsync(
        catalogId: String,
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): Result<DeepSkyObjectData>

    suspend fun transformCoordinatesAsync(
        fromType: CoordinateType,
        toType: CoordinateType,
        coordinate1: Double,
        coordinate2: Double,
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): Result<CoordinateTransformResult>

    suspend fun getAtmosphericCorrectionAsync(
        altitude: Double,
        azimuth: Double,
        temperature: Double,
        pressure: Double,
        humidity: Double
    ): Result<AtmosphericCorrectionData>
}