// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/GetSubscriptionByTransactionIdQuery.kt
package com.x3squaredcircles.photography.application.queries.subscription

data class GetSubscriptionByTransactionIdQuery(
    val transactionId: String
)

data class GetSubscriptionByTransactionIdQueryResult(
    val subscription: SubscriptionDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)