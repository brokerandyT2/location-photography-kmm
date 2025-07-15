// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/GetPagedTipTypesQuery.kt
package com.x3squaredcircles.photography.application.queries.tiptype

import com.x3squaredcircles.core.domain.entities.TipType

data class GetPagedTipTypesQuery(
    val pageNumber: Int,
    val pageSize: Int
)

data class GetPagedTipTypesQueryResult(
    val tipTypes: List<TipType>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)