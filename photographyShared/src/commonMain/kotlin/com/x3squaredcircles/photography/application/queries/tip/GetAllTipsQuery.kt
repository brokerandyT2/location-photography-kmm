// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/GetAllTipsQuery.kt
package com.x3squaredcircles.photography.application.queries.tip

import com.x3squaredcircles.core.domain.entities.Tip

data class GetAllTipsQuery(
    val dummy: Boolean = true
)

data class GetAllTipsQueryResult(
    val tips: List<Tip>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)