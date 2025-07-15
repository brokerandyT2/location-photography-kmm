// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/GetTipByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.tip

import com.x3squaredcircles.core.domain.entities.Tip

data class GetTipByIdQuery(
    val id: Int
)

data class GetTipByIdQueryResult(
    val tip: Tip?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)