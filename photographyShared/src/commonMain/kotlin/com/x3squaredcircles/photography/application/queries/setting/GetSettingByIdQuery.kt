// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/setting/GetSettingByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.setting

import com.x3squaredcircles.core.domain.entities.Setting

data class GetSettingByIdQuery(
    val id: Int
)

data class GetSettingByIdQueryResult(
    val setting: Setting?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)