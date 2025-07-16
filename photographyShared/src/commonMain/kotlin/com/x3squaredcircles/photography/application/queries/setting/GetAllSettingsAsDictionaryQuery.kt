// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/GetAllSettingsAsDictionaryQuery.kt
package com.x3squaredcircles.photography.application.queries.setting

data class GetAllSettingsAsDictionaryQuery(
    val dummy: Boolean = true
)

data class GetAllSettingsAsDictionaryQueryResult(
    val settings: Map<String, String>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)