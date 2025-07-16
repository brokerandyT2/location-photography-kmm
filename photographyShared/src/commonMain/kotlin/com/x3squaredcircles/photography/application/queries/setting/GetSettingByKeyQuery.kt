// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/GetSettingByKeyQuery.kt
package com.x3squaredcircles.photography.application.queries.setting

import com.x3squaredcircles.core.domain.entities.Setting

data class GetSettingByKeyQuery(
    val key: String
)

data class GetSettingByKeyQueryResult(
    val setting: Setting?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)