// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/LocalSubscriptionInfo.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class LocalSubscriptionInfo(
    val subscriptionType: String = "Free",
    val expirationDate: Instant? = null,
    val isActive: Boolean = false,
    val lastVerified: Instant? = null,
    val transactionId: String? = null,
    val purchaseToken: String? = null,
    val userId: String? = null
)