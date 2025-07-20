// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/CheckSubscriptionStatusCommand.kt
package com.x3squaredcircles.photography.application.commands.subscription

import com.x3squaredcircles.photography.domain.models.SubscriptionStatusDto

data class CheckSubscriptionStatusCommand(
    val dummy: Boolean = true
)

data class CheckSubscriptionStatusCommandResult(
    val status: SubscriptionStatusDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)