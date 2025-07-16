// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/GetExpiredSubscriptionsQuery.kt
package com.x3squaredcircles.photography.application.queries.subscription

data class GetExpiredSubscriptionsQuery(
    val currentTime: Long
)

data class GetExpiredSubscriptionsQueryResult(
    val subscriptions: List<SubscriptionDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)