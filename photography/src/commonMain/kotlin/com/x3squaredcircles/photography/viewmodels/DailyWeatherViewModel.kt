// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/DailyWeatherViewModel.kt
package com.x3squaredcircles.photography.viewmodels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant
class DailyWeatherViewModel {
    private val _date = MutableStateFlow(Instant.DISTANT_PAST)
    val date: StateFlow<Instant> = _date.asStateFlow()

    private val _dayName = MutableStateFlow("")
    val dayName: StateFlow<String> = _dayName.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _minTemperature = MutableStateFlow("")
    val minTemperature: StateFlow<String> = _minTemperature.asStateFlow()

    private val _maxTemperature = MutableStateFlow("")
    val maxTemperature: StateFlow<String> = _maxTemperature.asStateFlow()

    private val _weatherIcon = MutableStateFlow("")
    val weatherIcon: StateFlow<String> = _weatherIcon.asStateFlow()

    private val _sunriseTime = MutableStateFlow("")
    val sunriseTime: StateFlow<String> = _sunriseTime.asStateFlow()

    private val _sunsetTime = MutableStateFlow("")
    val sunsetTime: StateFlow<String> = _sunsetTime.asStateFlow()

    private val _windDirection = MutableStateFlow(0.0)
    val windDirection: StateFlow<Double> = _windDirection.asStateFlow()

    private val _windSpeed = MutableStateFlow("")
    val windSpeed: StateFlow<String> = _windSpeed.asStateFlow()

    private val _windGust = MutableStateFlow("")
    val windGust: StateFlow<String> = _windGust.asStateFlow()

    private val _isToday = MutableStateFlow(false)
    val isToday: StateFlow<Boolean> = _isToday.asStateFlow()

    fun setDate(value: Instant) {
        _date.value = value
    }

    fun setDayName(value: String) {
        _dayName.value = value
    }

    fun setDescription(value: String) {
        _description.value = value
    }

    fun setMinTemperature(value: String) {
        _minTemperature.value = value
    }

    fun setMaxTemperature(value: String) {
        _maxTemperature.value = value
    }

    fun setWeatherIcon(value: String) {
        _weatherIcon.value = value
    }

    fun setSunriseTime(value: String) {
        _sunriseTime.value = value
    }

    fun setSunsetTime(value: String) {
        _sunsetTime.value = value
    }

    fun setWindDirection(value: Double) {
        _windDirection.value = value
    }

    fun setWindSpeed(value: String) {
        _windSpeed.value = value
    }

    fun setWindGust(value: String) {
        _windGust.value = value
    }

    fun setIsToday(value: Boolean) {
        _isToday.value = value
    }
}