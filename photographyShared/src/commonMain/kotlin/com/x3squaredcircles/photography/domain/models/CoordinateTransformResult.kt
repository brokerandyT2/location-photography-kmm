// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/CoordinateTransformResult.kt
package com.x3squaredcircles.photography.domain.models

import com.x3squaredcircles.photography.domain.enums.CoordinateType
import kotlinx.datetime.Instant

data class CoordinateTransformResult(
    val fromType: CoordinateType,
    val toType: CoordinateType,
    val inputCoordinate1: Double,
    val inputCoordinate2: Double,
    val outputCoordinate1: Double,
    val outputCoordinate2: Double,
    val dateTime: Instant,
    val latitude: Double,
    val longitude: Double
)