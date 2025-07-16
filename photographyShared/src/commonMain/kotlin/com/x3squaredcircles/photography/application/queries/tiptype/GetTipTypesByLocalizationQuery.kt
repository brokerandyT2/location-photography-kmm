// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/GetTipTypesByLocalizationQuery.kt
package com.x3squaredcircles.photography.application.queries.tiptype

import com.x3squaredcircles.core.domain.entities.TipType

data class GetTipTypesByLocalizationQuery(
    val localization: String
)

data class GetTipTypesByLocalizationQueryResult(
    val tipTypes: List<TipType>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)