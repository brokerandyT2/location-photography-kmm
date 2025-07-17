// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/SubscriptionModels.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class ProcessSubscriptionResultDto(
    val productId: String,
    val transactionId: String,
    val purchaseToken: String,
    val purchaseDate: Instant,
    val expirationDate: Instant,
    val status: SubscriptionStatus
)

data class SubscriptionStatusDto(
    val hasActiveSubscription: Boolean = false,
    val productId: String? = null,
    val status: SubscriptionStatus = SubscriptionStatus.NONE,
    val expirationDate: Instant? = null,
    val purchaseDate: Instant? = null,
    val period: SubscriptionPeriod = SubscriptionPeriod.NONE,
    val isExpiringSoon: Boolean = false,
    val daysUntilExpiration: Int = 0
)

enum class SubscriptionStatus {
    NONE,
    ACTIVE,
    EXPIRED,
    CANCELLED,
    FAILED,
    PENDING
}

enum class SubscriptionPeriod {
    NONE,
    MONTHLY,
    YEARLY
}