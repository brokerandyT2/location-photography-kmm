// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/GetSettingsCountQuery.kt
package com.x3squaredcircles.photography.application.queries.setting

data class GetSettingsCountQuery(
    val dummy: Boolean = true
)

data class GetSettingsCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)