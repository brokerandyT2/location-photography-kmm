// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/GetAllTipTypesQuery.kt
package com.x3squaredcircles.photography.application.queries.tiptype

import com.x3squaredcircles.core.domain.entities.TipType

data class GetAllTipTypesQuery(
    val dummy: Boolean = true
)

data class GetAllTipTypesQueryResult(
    val tipTypes: List<TipType>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)