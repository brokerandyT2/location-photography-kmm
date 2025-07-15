// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/GetTipTypeByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.tiptype

import com.x3squaredcircles.core.domain.entities.TipType

data class GetTipTypeByIdQuery(
    val id: Int
)

data class GetTipTypeByIdQueryResult(
    val tipType: TipType?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)