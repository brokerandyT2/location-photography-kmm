// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/MeteorShowerData.kt
package com.x3squaredcircles.photography.domain.entities

import kotlinx.datetime.LocalDate

data class MeteorShowerData(
    val showers: List<MeteorShower> = emptyList()
) {
    fun getActiveShowers(date: LocalDate): List<MeteorShower> {
        return showers.filter { it.isActiveOn(date) }
    }

    fun getActiveShowers(date: LocalDate, minZHR: Int): List<MeteorShower> {
        return showers
            .filter { it.isActiveOn(date) && it.getExpectedZHR(date) >= minZHR }
            .sortedByDescending { it.getExpectedZHR(date) }
    }

    fun getShowerByCode(code: String): MeteorShower? {
        return showers.firstOrNull {
            it.code.equals(code, ignoreCase = true)
        }
    }

    fun getPeakShowers(date: LocalDate): List<MeteorShower> {
        return showers.filter { it.activity.peakDate == date }
    }
}