// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/GetTipTypeByNameQuery.kt
package com.x3squaredcircles.photography.application.queries.tiptype

import com.x3squaredcircles.core.domain.entities.TipType

data class GetTipTypeByNameQuery(
    val name: String
)

data class GetTipTypeByNameQueryResult(
    val tipType: TipType?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)