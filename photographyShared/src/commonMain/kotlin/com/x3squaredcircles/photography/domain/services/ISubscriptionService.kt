// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/ISubscriptionService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.models.ProcessSubscriptionResultDto
import com.x3squaredcircles.photography.domain.models.SubscriptionStatusDto

interface ISubscriptionService {

    suspend fun validateAndUpdateSubscriptionAsync(): Result<Boolean>

    suspend fun purchaseSubscriptionAsync(productId: String): Result<ProcessSubscriptionResultDto>

    suspend fun storeSubscriptionAsync(subscriptionData: ProcessSubscriptionResultDto): Result<Boolean>

    suspend fun getCurrentSubscriptionStatusAsync(): Result<SubscriptionStatusDto>

    suspend fun getAvailableSubscriptionProductsAsync(): Result<List<String>>

    suspend fun restoreSubscriptionsAsync(): Result<Boolean>
}