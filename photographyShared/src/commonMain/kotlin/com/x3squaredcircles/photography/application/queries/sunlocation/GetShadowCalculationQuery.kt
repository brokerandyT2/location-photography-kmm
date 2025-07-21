// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/sunlocation/GetShadowCalculationQuery.kt
package com.x3squaredcircles.photography.application.queries.sunlocation

import kotlinx.datetime.Instant

data class GetShadowCalculationQuery(
    val latitude: Double,
    val longitude: Double,
    val dateTime: Instant,
    val objectHeight: Double = 6.0, // Default 6 feet
    val terrainType: TerrainType = TerrainType.FLAT
)

data class GetShadowCalculationQueryResult(
    val shadowCalculation: ShadowCalculationResultDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class ShadowCalculationResultDto(
    val shadowLength: Double,
    val shadowDirection: Double,
    val objectHeight: Double,
    val calculationTime: Instant,
    val terrain: TerrainType,
    val shadowProgression: List<ShadowTimePointDto>
)

data class ShadowTimePointDto(
    val time: Instant,
    val length: Double,
    val direction: Double
)

enum class TerrainType {
    FLAT,
    URBAN,
    FOREST,
    MOUNTAIN,
    BEACH
}