// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/GetSubscriptionByPurchaseTokenQuery.kt
package com.x3squaredcircles.photography.application.queries.subscription

data class GetSubscriptionByPurchaseTokenQuery(
    val purchaseToken: String
)

data class GetSubscriptionByPurchaseTokenQueryResult(
    val subscription: SubscriptionDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)