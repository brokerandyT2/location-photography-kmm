// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/GetAllSettingsQuery.kt
package com.x3squaredcircles.photography.application.queries.setting

import com.x3squaredcircles.core.domain.entities.Setting

data class GetAllSettingsQuery(
    val dummy: Boolean = true
)

data class GetAllSettingsQueryResult(
    val settings: List<Setting>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)