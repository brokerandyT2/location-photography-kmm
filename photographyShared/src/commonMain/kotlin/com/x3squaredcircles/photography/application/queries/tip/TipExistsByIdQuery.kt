// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/TipExistsByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.tip

data class TipExistsByIdQuery(
    val id: Int
)

data class TipExistsByIdQueryResult(
    val exists: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)