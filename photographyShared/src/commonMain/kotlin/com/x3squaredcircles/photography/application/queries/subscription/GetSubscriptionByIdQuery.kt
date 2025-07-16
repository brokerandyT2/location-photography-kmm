// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/GetSubscriptionByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.subscription

data class GetSubscriptionByIdQuery(
    val id: Int
)

data class GetSubscriptionByIdQueryResult(
    val subscription: SubscriptionDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)