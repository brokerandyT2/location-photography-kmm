// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/common/interfaces/IMeteorShowerDataService.kt
package com.x3squaredcircles.photography.application.common.interfaces

import com.x3squaredcircles.photography.domain.entities.MeteorShower
import kotlinx.datetime.LocalDate

interface IMeteorShowerDataService {
    suspend fun getActiveShowersAsync(date: LocalDate): List<MeteorShower>
    suspend fun getActiveShowersAsync(date: LocalDate, minZHR: Int): List<MeteorShower>
    suspend fun getShowerByCodeAsync(code: String): MeteorShower?
    suspend fun getAllShowersAsync(): List<MeteorShower>
    suspend fun getPeakShowersAsync(date: LocalDate): List<MeteorShower>
}