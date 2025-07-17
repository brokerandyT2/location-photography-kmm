// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/IAstroCalculationService.kt
package com.x3squaredcircles.photography.domain.services

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
    ): PlanetPositionData

    suspend fun getVisiblePlanetsAsync(
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): List<PlanetPositionData>

    suspend fun getPlanetaryConjunctionsAsync(
        startDate: Instant,
        endDate: Instant,
        latitude: Double,
        longitude: Double
    ): List<PlanetaryConjunction>

    suspend fun getPlanetOppositionsAsync(
        startDate: Instant,
        endDate: Instant
    ): List<PlanetaryEvent>

    suspend fun getEnhancedMoonDataAsync(
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): EnhancedMoonData

    suspend fun getConstellationDataAsync(
        constellation: ConstellationType,
        date: Instant,
        latitude: Double,
        longitude: Double
    ): ConstellationData

    suspend fun getDeepSkyObjectDataAsync(
        catalogId: String,
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): DeepSkyObjectData

    suspend fun transformCoordinatesAsync(
        fromType: CoordinateType,
        toType: CoordinateType,
        coordinate1: Double,
        coordinate2: Double,
        dateTime: Instant,
        latitude: Double,
        longitude: Double
    ): CoordinateTransformResult

    suspend fun getAtmosphericCorrectionAsync(
        altitude: Double,
        azimuth: Double,
        temperature: Double,
        pressure: Double,
        humidity: Double
    ): AtmosphericCorrectionData
}