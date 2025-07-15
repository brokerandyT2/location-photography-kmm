// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/GetTipTypesWithTipCountsQuery.kt
package com.x3squaredcircles.photography.application.queries.tiptype

import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.TipTypeWithCount

data class GetTipTypesWithTipCountsQuery(
    val dummy: Boolean = true
)

data class GetTipTypesWithTipCountsQueryResult(
    val tipTypes: List<TipTypeWithCount>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)