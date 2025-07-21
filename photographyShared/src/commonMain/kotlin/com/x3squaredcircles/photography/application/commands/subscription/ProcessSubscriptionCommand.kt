// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/ProcessSubscriptionCommand.kt
package com.x3squaredcircles.photography.application.commands.subscription

import com.x3squaredcircles.photography.domain.models.SubscriptionPeriod
import com.x3squaredcircles.photography.domain.models.SubscriptionStatus
import com.x3squaredcircles.photography.domain.models.ProcessSubscriptionResultDto

data class ProcessSubscriptionCommand(
    val productId: String = "",
    val period: SubscriptionPeriod = SubscriptionPeriod.NONE
)

data class ProcessSubscriptionCommandResult(
    val result: ProcessSubscriptionResultDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)