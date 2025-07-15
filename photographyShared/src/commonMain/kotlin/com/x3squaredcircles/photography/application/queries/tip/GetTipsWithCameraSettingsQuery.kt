// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/tip/GetTipsWithCameraSettingsQuery.kt
package com.x3squaredcircles.photography.application.queries.tip

import com.x3squaredcircles.core.domain.entities.Tip

data class GetTipsWithCameraSettingsQuery(
    val dummy: Boolean = true
)

data class GetTipsWithCameraSettingsQueryResult(
    val tips: List<Tip>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)