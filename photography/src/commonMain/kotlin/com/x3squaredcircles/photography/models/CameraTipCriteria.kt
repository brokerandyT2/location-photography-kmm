// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/CameraTipCriteria.kt
package com.x3squaredcircles.photography.models

data class CameraTipCriteria(
    val aperture: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val lightCondition: String = "",
    val timePeriod: String = "",
    val optimalForPortraits: Boolean = false,
    val optimalForLandscapes: Boolean = false,
    val weatherCondition: String = "",
    val equipmentRecommendation: String = ""
)