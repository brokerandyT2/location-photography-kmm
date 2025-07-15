// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/GetTipsTotalCountQuery.kt
package com.x3squaredcircles.photography.application.queries.tip

data class GetTipsTotalCountQuery(
    val dummy: Boolean = true
)

data class GetTipsTotalCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)