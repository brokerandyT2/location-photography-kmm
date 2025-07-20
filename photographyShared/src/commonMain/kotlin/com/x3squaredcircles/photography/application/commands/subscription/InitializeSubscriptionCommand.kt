// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/InitializeSubscriptionCommand.kt
package com.x3squaredcircles.photography.application.commands.subscription

import com.x3squaredcircles.photography.application.queries.subscription.SubscriptionDto

data class InitializeSubscriptionCommand(
    val dummy: Boolean = true
)

data class InitializeSubscriptionCommandResult(
    val products: List<SubscriptionProductDto>,
    val isConnected: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class SubscriptionProductDto(
    val productId: String,
    val title: String,
    val description: String,
    val price: String,
    val priceAmountMicros: String,
    val currencyCode: String,
    val period: SubscriptionPeriod
)

enum class SubscriptionPeriod {
    MONTHLY,
    YEARLY,
    WEEKLY,
    LIFETIME
}