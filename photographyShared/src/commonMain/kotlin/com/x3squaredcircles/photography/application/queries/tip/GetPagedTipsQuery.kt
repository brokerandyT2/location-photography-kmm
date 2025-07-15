// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/GetPagedTipsQuery.kt
package com.x3squaredcircles.photography.application.queries.tip

import com.x3squaredcircles.core.domain.entities.Tip

data class GetPagedTipsQuery(
    val pageNumber: Int,
    val pageSize: Int,
    val tipTypeId: Int? = null
)

data class GetPagedTipsQueryResult(
    val tips: List<Tip>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)