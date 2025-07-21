// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/GetSubscriptionStatusQuery.kt
package com.x3squaredcircles.photography.application.queries.subscription

import com.x3squaredcircles.photography.domain.models.SubscriptionStatusDto

data class GetSubscriptionStatusQuery(
    val dummy: Boolean = true
)

data class GetSubscriptionStatusQueryResult(
    val status: SubscriptionStatusDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)