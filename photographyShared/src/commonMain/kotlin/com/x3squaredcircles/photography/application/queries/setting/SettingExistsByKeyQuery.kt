// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/SettingExistsByKeyQuery.kt
package com.x3squaredcircles.photography.application.queries.setting

data class SettingExistsByKeyQuery(
    val key: String
)

data class SettingExistsByKeyQueryResult(
    val exists: Boolean,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)