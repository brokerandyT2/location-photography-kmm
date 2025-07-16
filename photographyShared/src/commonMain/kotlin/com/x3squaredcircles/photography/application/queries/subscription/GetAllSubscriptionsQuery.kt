// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/GetAllSubscriptionsQuery.kt
package com.x3squaredcircles.photography.application.queries.subscription

data class GetAllSubscriptionsQuery(
    val dummy: Boolean = true
)

data class GetAllSubscriptionsQueryResult(
    val subscriptions: List<SubscriptionDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class SubscriptionDto(
    val id: Int,
    val userId: String,
    val transactionId: String,
    val purchaseToken: String,
    val productId: String,
    val isActive: Boolean,
    val expirationDate: Long,
    val purchaseDate: Long,
    val lastVerified: Long
)