// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/GetRandomTipsQuery.kt
package com.x3squaredcircles.photography.application.queries.tip

import com.x3squaredcircles.core.domain.entities.Tip

data class GetRandomTipsQuery(
    val count: Int = 1
)

data class GetRandomTipsQueryResult(
    val tips: List<Tip>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)