// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/MeteorShowerActivity.kt
package com.x3squaredcircles.photography.domain.entities

import kotlinx.datetime.LocalDate
import kotlin.math.abs

data class MeteorShowerActivity(
    val startDate: LocalDate,
    val peakDate: LocalDate,
    val endDate: LocalDate,
    val peakZHR: Int,
    val variable: String = ""
) {
    fun isActiveOn(date: LocalDate): Boolean {
        return date >= startDate && date <= endDate
    }

    fun getExpectedZHR(date: LocalDate): Double {
        if (!isActiveOn(date)) return 0.0

        val daysFromPeak = abs(date.toEpochDays() - peakDate.toEpochDays())

        if (daysFromPeak == 0) return peakZHR.toDouble()

        val totalDays = (endDate.toEpochDays() - startDate.toEpochDays()).toDouble()
        val peakPosition = (peakDate.toEpochDays() - startDate.toEpochDays()).toDouble()

        val falloffFactor = when {
            daysFromPeak <= 1 -> 0.8
            daysFromPeak <= 2 -> 0.5
            daysFromPeak <= 3 -> 0.3
            daysFromPeak <= 5 -> 0.1
            else -> 0.05
        }

        return (peakZHR * falloffFactor).coerceAtLeast(1.0)
    }

    val durationDays: Int
        get() = (endDate.toEpochDays() - startDate.toEpochDays()).toInt()

    val isPeak: Boolean
        get() = peakDate == startDate || peakDate == endDate
}