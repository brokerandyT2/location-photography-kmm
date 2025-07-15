// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/SearchTipsByTextQuery.kt
package com.x3squaredcircles.photography.application.queries.tip

import com.x3squaredcircles.core.domain.entities.Tip

data class SearchTipsByTextQuery(
    val searchTerm: String
)

data class SearchTipsByTextQueryResult(
    val tips: List<Tip>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)