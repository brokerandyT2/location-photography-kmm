// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/AstroCalculationResult.kt
package com.x3squaredcircles.photography.domain.models

import com.x3squaredcircles.photography.domain.enums.AstroTarget
import kotlinx.datetime.Instant

data class AstroCalculationResult(
    val target: AstroTarget,
    val calculationTime: Instant,
    val localTime: Instant,
    val setTime: Instant? = null,
    val optimalTime: Instant? = null,
    val azimuth: Double,
    val altitude: Double,
    val isVisible: Boolean,
    val description: String = "",
    val equipment: String = ""
)