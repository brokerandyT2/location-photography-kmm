// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/GetTipsByLocalizationQuery.kt
package com.x3squaredcircles.photography.application.queries.tip

import com.x3squaredcircles.core.domain.entities.Tip

data class GetTipsByLocalizationQuery(
    val localization: String
)

data class GetTipsByLocalizationQueryResult(
    val tips: List<Tip>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)