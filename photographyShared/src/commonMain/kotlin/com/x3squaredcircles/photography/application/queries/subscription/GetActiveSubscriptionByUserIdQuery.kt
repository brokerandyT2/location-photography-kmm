// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/GetActiveSubscriptionByUserIdQuery.kt
package com.x3squaredcircles.photography.application.queries.subscription

data class GetActiveSubscriptionByUserIdQuery(
    val userId: String,
    val currentTime: Long
)

data class GetActiveSubscriptionByUserIdQueryResult(
    val subscription: SubscriptionDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)