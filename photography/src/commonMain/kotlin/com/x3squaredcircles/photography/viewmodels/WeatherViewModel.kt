// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/WeatherViewModel.kt
package com.x3squaredcircles.photography.viewmodels
import com.x3squaredcircles.photography.dtos.WeatherForecastDto
import com.x3squaredcircles.photography.dtos.DailyForecastDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.mutableMapOf
class WeatherViewModel : BaseViewModel() {
    private val _locationId = MutableStateFlow(0)
    val locationId: StateFlow<Int> = _locationId.asStateFlow()

    private val _dailyForecasts = MutableStateFlow<List<DailyWeatherViewModel>>(emptyList())
    val dailyForecasts: StateFlow<List<DailyWeatherViewModel>> = _dailyForecasts.asStateFlow()

    private val _weatherForecast = MutableStateFlow<WeatherForecastDto?>(null)
    val weatherForecast: StateFlow<WeatherForecastDto?> = _weatherForecast.asStateFlow()

    // Cache for icon URLs to avoid repeated string operations
    private val iconUrlCache = mutableMapOf<String, String>()

    // Pre-compiled formatters
    private val dayFormats = arrayOf("dddd, MMMM d")
    private val temperatureFormat = "F1"
    private val timeFormat = "t"
    private val windSpeedFormat = "F1"

    init {
        initializeIconCache()
    }

    fun setLocationId(value: Int) {
        _locationId.value = value
    }

    fun setWeatherForecast(value: WeatherForecastDto?) {
        _weatherForecast.value = value
    }

    suspend fun loadWeatherAsync(locationId: Int) {
        try {
            setBusy(true)
            clearErrors()
            setLocationId(locationId)

            // TODO: Implement weather update command through mediator
            // Need UpdateWeatherCommand and mediator dependency
            // val weatherTask = mediator.send(UpdateWeatherCommand(locationId = locationId, forceUpdate = true))
            // val result = weatherTask
            // if (!result.isSuccess || result.data == null) {
            //     onSystemError(result.errorMessage ?: "Failed to load weather data")
            //     return
            // }
            // val weatherData = result.data

            // TODO: Implement forecast query through mediator
            // Need GetWeatherForecastQuery and mediator dependency
            // val forecastTask = mediator.send(GetWeatherForecastQuery(
            //     latitude = weatherData.latitude,
            //     longitude = weatherData.longitude,
            //     days = 5
            // ))
            // val forecastResult = forecastTask
            // if (!forecastResult.isSuccess || forecastResult.data == null) {
            //     onSystemError(forecastResult.errorMessage ?: "Failed to load forecast data")
            //     return
            // }
            // setWeatherForecast(forecastResult.data)
            // processForecastDataOptimized(forecastResult.data)

        } catch (ex: Exception) {
            onSystemError("Error loading weather data: ${ex.message}")
        } finally {
            setBusy(false)
        }
    }

    private suspend fun processForecastDataOptimized(forecast: WeatherForecastDto) {
        if (forecast.dailyForecasts.isEmpty()) {
            setValidationError("No forecast data available")
            return
        }

        // TODO: Process forecast data and update _dailyForecasts
        // Need to convert forecast data to DailyWeatherViewModel instances
        // val processedItems = forecast.dailyForecasts.take(5).mapIndexed { index, dailyForecast ->
        //     createDailyWeatherViewModel(dailyForecast, index == 0)
        // }
        // _dailyForecasts.value = processedItems
    }

    private fun createDailyWeatherViewModel(dailyForecast: DailyForecastDto, isToday: Boolean): DailyWeatherViewModel {
        // TODO: Implement conversion from forecast data to view model
        // Need proper forecast data type and DailyWeatherViewModel implementation
        return DailyWeatherViewModel()
    }

    private fun getWeatherIconUrlCached(iconCode: String): String {
        if (iconCode.isEmpty()) return "weather_unknown.png"

        return iconUrlCache.getOrPut(iconCode) { "a$iconCode.png" }
    }

    private fun initializeIconCache() {
        val commonIcons = arrayOf(
            "01d", "01n", "02d", "02n", "03d", "03n", "04d", "04n",
            "09d", "09n", "10d", "10n", "11d", "11n", "13d", "13n", "50d", "50n"
        )

        commonIcons.forEach { icon ->
            iconUrlCache[icon] = "a$icon.png"
        }
    }

    fun onNavigatedToAsync() {
        // TODO: Implement navigation logic
    }

    fun onNavigatedFromAsync() {
        // TODO: Implement navigation logic
    }

    override fun dispose() {
        iconUrlCache.clear()
        super.dispose()
    }
}