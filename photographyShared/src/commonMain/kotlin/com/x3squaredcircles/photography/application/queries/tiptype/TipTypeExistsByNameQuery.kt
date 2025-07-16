// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tiptype/TipTypeExistsByNameQuery.kt
package com.x3squaredcircles.photography.application.queries.tiptype

data class TipTypeExistsByNameQuery(
    val name: String,
    val excludeId: Int = 0
)

data class TipTypeExistsByNameQueryResult(
    val exists: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)