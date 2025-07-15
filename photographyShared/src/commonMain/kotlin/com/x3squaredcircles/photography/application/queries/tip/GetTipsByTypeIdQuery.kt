// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/GetTipsByTypeIdQuery.kt
package com.x3squaredcircles.photography.application.queries.tip

import com.x3squaredcircles.core.domain.entities.Tip

data class GetTipsByTypeIdQuery(
    val tipTypeId: Int
)

data class GetTipsByTypeIdQueryResult(
    val tips: List<Tip>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)