// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/SimpleLocationViewModel.kt
package com.x3squaredcircles.photography.models

data class SimpleLocationViewModel(
    val name: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val photo: String = "",
    val id: Int = 0
)