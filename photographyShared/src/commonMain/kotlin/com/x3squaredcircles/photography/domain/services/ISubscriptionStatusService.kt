// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/ISubscriptionStatusService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.models.LocalSubscriptionInfo
import com.x3squaredcircles.photography.domain.models.SubscriptionStatusResult

interface ISubscriptionStatusService {

    suspend fun checkSubscriptionStatusAsync(): Result<SubscriptionStatusResult>

    suspend fun canAccessPremiumFeaturesAsync(): Result<Boolean>

    suspend fun canAccessProFeaturesAsync(): Result<Boolean>

    suspend fun isInGracePeriodAsync(): Result<Boolean>

    suspend fun getLocalSubscriptionInfoAsync(): Result<LocalSubscriptionInfo>
}