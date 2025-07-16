// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/GetSubscriptionsCountQuery.kt
package com.x3squaredcircles.photography.application.queries.subscription

data class GetSubscriptionsCountQuery(
    val dummy: Boolean = true
)

data class GetSubscriptionsCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)