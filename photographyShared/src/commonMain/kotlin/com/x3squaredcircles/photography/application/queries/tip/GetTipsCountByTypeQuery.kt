// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/GetTipsCountByTypeQuery.kt
package com.x3squaredcircles.photography.application.queries.tip

data class GetTipsCountByTypeQuery(
    val tipTypeId: Int
)

data class GetTipsCountByTypeQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)