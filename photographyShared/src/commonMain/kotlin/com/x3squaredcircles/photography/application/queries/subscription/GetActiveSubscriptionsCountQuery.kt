// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/GetActiveSubscriptionsCountQuery.kt
package com.x3squaredcircles.photography.application.queries.subscription

data class GetActiveSubscriptionsCountQuery(
    val currentTime: Long
)

data class GetActiveSubscriptionsCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)