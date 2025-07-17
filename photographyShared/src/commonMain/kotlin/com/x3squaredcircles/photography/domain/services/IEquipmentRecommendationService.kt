// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/IEquipmentRecommendationService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.models.AstroTarget
import com.x3squaredcircles.photography.domain.models.UserEquipmentRecommendation
import com.x3squaredcircles.photography.domain.models.HourlyEquipmentRecommendation
import com.x3squaredcircles.photography.domain.models.GenericEquipmentRecommendation
import kotlinx.datetime.Instant

interface IEquipmentRecommendationService {

    suspend fun getUserEquipmentRecommendationAsync(
        target: AstroTarget
    ): Result<UserEquipmentRecommendation>

    suspend fun getHourlyEquipmentRecommendationsAsync(
        target: AstroTarget,
        predictionTimes: List<Instant>
    ): Result<List<HourlyEquipmentRecommendation>>

    suspend fun getGenericRecommendationAsync(
        target: AstroTarget
    ): Result<GenericEquipmentRecommendation>
}