// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/AtmosphericCorrectionData.kt
package com.x3squaredcircles.photography.domain.models

data class AtmosphericCorrectionData(
    val trueAltitude: Double,
    val apparentAltitude: Double,
    val refractionCorrection: Double,
    val atmosphericExtinction: Double,
    val correctionNotes: String
)