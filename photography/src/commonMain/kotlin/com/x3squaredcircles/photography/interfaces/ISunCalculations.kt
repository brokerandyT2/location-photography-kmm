// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/interfaces/ISunCalculations.kt
package com.x3squaredcircles.photography.interfaces

import com.x3squaredcircles.photography.viewmodels.OperationErrorEventArgs
import com.x3squaredcircles.photography.viewmodels.LocationViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime

interface ISunCalculations {
    // Location and date properties
    val latitude: StateFlow<Double>
    val longitude: StateFlow<Double>
    val date: StateFlow<LocalDateTime>
    val dateFormat: StateFlow<String>
    val timeFormat: StateFlow<String>

    // Location selection
    val selectedLocation: StateFlow<LocationViewModel?>
    val locationPhoto: StateFlow<String>

    // Sun time properties
    val sunrise: StateFlow<Instant>
    val sunset: StateFlow<Instant>
    val solarNoon: StateFlow<Instant>
    val astronomicalDawn: StateFlow<Instant>
    val nauticalDawn: StateFlow<Instant>
    val nauticalDusk: StateFlow<Instant>
    val astronomicalDusk: StateFlow<Instant>
    val civilDawn: StateFlow<Instant>
    val civilDusk: StateFlow<Instant>

    // Formatted time display properties
    val sunRiseFormatted: StateFlow<String>
    val sunSetFormatted: StateFlow<String>
    val solarNoonFormatted: StateFlow<String>
    val goldenHourMorningFormatted: StateFlow<String>
    val goldenHourEveningFormatted: StateFlow<String>
    val astronomicalDawnFormatted: StateFlow<String>
    val astronomicalDuskFormatted: StateFlow<String>
    val nauticalDawnFormatted: StateFlow<String>
    val nauticalDuskFormatted: StateFlow<String>
    val civilDawnFormatted: StateFlow<String>
    val civilDuskFormatted: StateFlow<String>

    // Error handling
    val errorOccurred: kotlinx.coroutines.flow.Flow<OperationErrorEventArgs>

    // Methods
    suspend fun calculateSun()
    suspend fun loadLocationsAsync()
}