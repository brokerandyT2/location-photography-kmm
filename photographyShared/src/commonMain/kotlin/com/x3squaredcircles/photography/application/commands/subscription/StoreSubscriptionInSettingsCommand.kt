// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/StoreSubscriptionInSettingsCommand.kt
package com.x3squaredcircles.photography.application.commands.subscription

import kotlinx.datetime.Instant

data class StoreSubscriptionInSettingsCommand(
    val productId: String = "",
    val expirationDate: Instant = Instant.DISTANT_PAST,
    val purchaseDate: Instant = Instant.DISTANT_PAST,
    val transactionId: String = ""
)

data class StoreSubscriptionInSettingsCommandResult(
    val isSuccess: Boolean,
    val errorMessage: String? = null
)