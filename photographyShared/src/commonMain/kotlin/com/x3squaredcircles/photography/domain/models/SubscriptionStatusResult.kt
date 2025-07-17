// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/SubscriptionStatusResult.kt
package com.x3squaredcircles.photography.domain.models

import kotlinx.datetime.Instant

data class SubscriptionStatusResult(
    val hasActiveSubscription: Boolean = false,
    val subscriptionType: String = "Free",
    val expirationDate: Instant? = null,
    val isInGracePeriod: Boolean = false,
    val networkCheckPerformed: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)