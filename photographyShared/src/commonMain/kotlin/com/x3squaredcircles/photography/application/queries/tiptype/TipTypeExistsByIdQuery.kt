// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/TipTypeExistsByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.tiptype

data class TipTypeExistsByIdQuery(
    val id: Int
)

data class TipTypeExistsByIdQueryResult(
    val exists: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)