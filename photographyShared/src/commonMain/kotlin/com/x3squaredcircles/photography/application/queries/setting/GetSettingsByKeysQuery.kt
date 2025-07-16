// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/GetSettingsByKeysQuery.kt
package com.x3squaredcircles.photography.application.queries.setting

import com.x3squaredcircles.core.domain.entities.Setting

data class GetSettingsByKeysQuery(
    val keys: List<String>
)

data class GetSettingsByKeysQueryResult(
    val settings: List<Setting>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)