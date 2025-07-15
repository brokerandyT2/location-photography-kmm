// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/GetTipTypesTotalCountQuery.kt
package com.x3squaredcircles.photography.application.queries.tiptype

data class GetTipTypesTotalCountQuery(
    val dummy: Boolean = true
)

data class GetTipTypesTotalCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)