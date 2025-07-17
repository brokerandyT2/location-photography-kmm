// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/MeteorShower.kt
package com.x3squaredcircles.photography.domain.entities

import kotlinx.datetime.LocalDate

data class MeteorShower(
    val code: String,
    val designation: String,
    val activity: MeteorShowerActivity,
    val radiantRA: Double,
    val radiantDec: Double,
    val speedKmS: Int,
    val parentBody: String
) {
    fun isActiveOn(date: LocalDate): Boolean {
        return activity.isActiveOn(date)
    }

    fun getExpectedZHR(date: LocalDate): Double {
        if (!isActiveOn(date)) return 0.0
        return activity.getExpectedZHR(date)
    }

    val directionDescription: String
        get() = when (azimuth) {
            in 337.5..360.0, in 0.0..22.5 -> "North"
            in 22.5..67.5 -> "Northeast"
            in 67.5..112.5 -> "East"
            in 112.5..157.5 -> "Southeast"
            in 157.5..202.5 -> "South"
            in 202.5..247.5 -> "Southwest"
            in 247.5..292.5 -> "West"
            in 292.5..337.5 -> "Northwest"
            else -> "Unknown"
        }

    private val azimuth: Double
        get() = radiantRA * 15.0 // Convert RA hours to degrees
}