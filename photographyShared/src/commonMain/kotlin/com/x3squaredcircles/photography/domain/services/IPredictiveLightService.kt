// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/IPredictiveLightService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.models.HourlyLightPrediction
import com.x3squaredcircles.photography.domain.models.PredictiveLightRecommendation
import com.x3squaredcircles.photography.domain.models.PredictiveLightRequest
import com.x3squaredcircles.photography.domain.models.WeatherImpactAnalysis
import com.x3squaredcircles.photography.domain.models.WeatherImpactAnalysisRequest

interface IPredictiveLightService {

    suspend fun analyzeWeatherImpactAsync(
        request: WeatherImpactAnalysisRequest
    ): Result<WeatherImpactAnalysis>

    suspend fun generateHourlyPredictionsAsync(
        request: PredictiveLightRequest
    ): Result<List<HourlyLightPrediction>>

    suspend fun generateRecommendationAsync(
        request: PredictiveLightRequest
    ): Result<PredictiveLightRecommendation>
}