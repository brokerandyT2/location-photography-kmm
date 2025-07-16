// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/dailyforecast/GetBestPhotographyDaysQuery.kt
package com.x3squaredcircles.photography.application.queries.dailyforecast

import com.x3squaredcircles.core.domain.entities.WeatherForecast

data class GetBestPhotographyDaysQuery(
    val weatherId: Int,
    val startDate: Long,
    val endDate: Long,
    val maxCloudCover: Double,
    val maxPrecipitationChance: Double,
    val limit: Int
)

data class GetBestPhotographyDaysQueryResult(
    val dailyForecasts: List<WeatherForecast>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)