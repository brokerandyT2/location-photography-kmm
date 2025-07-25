// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/SettingsViewModel.kt
package com.x3squaredcircles.photography.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class SettingsViewModel : BaseViewModel() {

    // Setting ViewModels
    private val _hemisphere = MutableStateFlow(
        SettingViewModel().apply {
            setKey("Hemisphere")
            setValue("North")
        }
    )
    val hemisphere: StateFlow<SettingViewModel> = _hemisphere.asStateFlow()

    private val _timeFormat = MutableStateFlow(
        SettingViewModel().apply {
            setKey("TimeFormat")
            setValue("12")
        }
    )
    val timeFormat: StateFlow<SettingViewModel> = _timeFormat.asStateFlow()

    private val _dateFormat = MutableStateFlow(
        SettingViewModel().apply {
            setKey("DateFormat")
            setValue("MM/dd/yyyy")
        }
    )
    val dateFormat: StateFlow<SettingViewModel> = _dateFormat.asStateFlow()

    private val _email = MutableStateFlow(
        SettingViewModel().apply {
            setKey("Email")
            setValue("")
        }
    )
    val email: StateFlow<SettingViewModel> = _email.asStateFlow()

    private val _windDirection = MutableStateFlow(
        SettingViewModel().apply {
            setKey("WindDirection")
            setValue("Degrees")
        }
    )
    val windDirection: StateFlow<SettingViewModel> = _windDirection.asStateFlow()

    private val _temperatureFormat = MutableStateFlow(
        SettingViewModel().apply {
            setKey("TemperatureFormat")
            setValue("Fahrenheit")
        }
    )
    val temperatureFormat: StateFlow<SettingViewModel> = _temperatureFormat.asStateFlow()

    private val _subscription = MutableStateFlow(
        SettingViewModel().apply {
            setKey("Subscription")
            setValue("Free")
        }
    )
    val subscription: StateFlow<SettingViewModel> = _subscription.asStateFlow()

    // Viewed flags
    private val _addLocationViewed = MutableStateFlow(
        SettingViewModel().apply {
            setKey("AddLocationViewed")
            setValue("false")
        }
    )
    val addLocationViewed: StateFlow<SettingViewModel> = _addLocationViewed.asStateFlow()

    private val _listLocationsViewed = MutableStateFlow(
        SettingViewModel().apply {
            setKey("ListLocationsViewed")
            setValue("false")
        }
    )
    val listLocationsViewed: StateFlow<SettingViewModel> = _listLocationsViewed.asStateFlow()

    private val _editLocationViewed = MutableStateFlow(
        SettingViewModel().apply {
            setKey("EditLocationViewed")
            setValue("false")
        }
    )
    val editLocationViewed: StateFlow<SettingViewModel> = _editLocationViewed.asStateFlow()

    private val _weatherViewed = MutableStateFlow(
        SettingViewModel().apply {
            setKey("WeatherViewed")
            setValue("false")
        }
    )
    val weatherViewed: StateFlow<SettingViewModel> = _weatherViewed.asStateFlow()

    private val _settingsViewed = MutableStateFlow(
        SettingViewModel().apply {
            setKey("SettingsViewed")
            setValue("false")
        }
    )
    val settingsViewed: StateFlow<SettingViewModel> = _settingsViewed.asStateFlow()

    private val _sunLocationViewed = MutableStateFlow(
        SettingViewModel().apply {
            setKey("SunLocationViewed")
            setValue("false")
        }
    )
    val sunLocationViewed: StateFlow<SettingViewModel> = _sunLocationViewed.asStateFlow()

    private val _sunCalculationViewed = MutableStateFlow(
        SettingViewModel().apply {
            setKey("SunCalculationViewed")
            setValue("false")
        }
    )
    val sunCalculationViewed: StateFlow<SettingViewModel> = _sunCalculationViewed.asStateFlow()

    private val _exposureCalculationViewed = MutableStateFlow(
        SettingViewModel().apply {
            setKey("ExposureCalculationViewed")
            setValue("false")
        }
    )
    val exposureCalculationViewed: StateFlow<SettingViewModel> = _exposureCalculationViewed.asStateFlow()

    private val _sceneEvaluationViewed = MutableStateFlow(
        SettingViewModel().apply {
            setKey("SceneEvaluationViewed")
            setValue("false")
        }
    )
    val sceneEvaluationViewed: StateFlow<SettingViewModel> = _sceneEvaluationViewed.asStateFlow()

    private val _subscriptionExpiration = MutableStateFlow(
        SettingViewModel().apply {
            setKey("SubscriptionExpiration")
            setValue(Clock.System.now().toString())
        }
    )
    val subscriptionExpiration: StateFlow<SettingViewModel> = _subscriptionExpiration.asStateFlow()

    // Boolean toggles
    private val _hemisphereNorth = MutableStateFlow(true)
    val hemisphereNorth: StateFlow<Boolean> = _hemisphereNorth.asStateFlow()

    private val _timeFormatToggle = MutableStateFlow(true)
    val timeFormatToggle: StateFlow<Boolean> = _timeFormatToggle.asStateFlow()

    private val _dateFormatToggle = MutableStateFlow(true)
    val dateFormatToggle: StateFlow<Boolean> = _dateFormatToggle.asStateFlow()

    private val _windDirectionBoolean = MutableStateFlow(true)
    val windDirectionBoolean: StateFlow<Boolean> = _windDirectionBoolean.asStateFlow()

    private val _temperatureFormatToggle = MutableStateFlow(true)
    val temperatureFormatToggle: StateFlow<Boolean> = _temperatureFormatToggle.asStateFlow()

    private val _adSupportBoolean = MutableStateFlow(false)
    val adSupportBoolean: StateFlow<Boolean> = _adSupportBoolean.asStateFlow()

    init {
        initializeSettings()
    }

    /**
     * PERFORMANCE OPTIMIZATION: Initialize all settings in one batch operation
     */
    private fun initializeSettings() {
        try {
            // All settings are already initialized with defaults in the StateFlow declarations
            // This method exists for consistency with C# version and future enhancement
        } catch (ex: Exception) {
            onSystemError("Error initializing settings: ${ex.message}")
        }
    }

    /**
     * PERFORMANCE OPTIMIZATION: Update multiple settings in one batch
     */
    fun updateSettingsBatch(settings: Map<String, String>) {
        try {
            settings.forEach { (key, value) ->
                updateSettingByKey(key, value)
            }
        } catch (ex: Exception) {
            onSystemError("Error updating settings batch: ${ex.message}")
        }
    }

    /**
     * PERFORMANCE OPTIMIZATION: Update individual setting without triggering immediate UI updates
     */
    private fun updateSettingByKey(key: String, value: String) {
        val settingToUpdate = when (key) {
            "Hemisphere" -> _hemisphere
            "TimeFormat" -> _timeFormat
            "DateFormat" -> _dateFormat
            "Email" -> _email
            "WindDirection" -> _windDirection
            "TemperatureFormat" -> _temperatureFormat
            "Subscription" -> _subscription
            "AddLocationViewed" -> _addLocationViewed
            "ListLocationsViewed" -> _listLocationsViewed
            "EditLocationViewed" -> _editLocationViewed
            "WeatherViewed" -> _weatherViewed
            "SettingsViewed" -> _settingsViewed
            "SunLocationViewed" -> _sunLocationViewed
            "SunCalculationViewed" -> _sunCalculationViewed
            "ExposureCalculationViewed" -> _exposureCalculationViewed
            "SceneEvaluationViewed" -> _sceneEvaluationViewed
            "SubscriptionExpiration" -> _subscriptionExpiration
            else -> null
        }

        settingToUpdate?.let { setting ->
            val currentSetting = setting.value
            currentSetting.setValue(value)
            currentSetting.setTimestamp(Clock.System.now())

            // Update boolean toggles based on setting values
            when (key) {
                "Hemisphere" -> _hemisphereNorth.value = value.equals("North", ignoreCase = true)
                "TimeFormat" -> _timeFormatToggle.value = value == "12"
                "DateFormat" -> _dateFormatToggle.value = value == "MM/dd/yyyy"
                "WindDirection" -> _windDirectionBoolean.value = value.equals("Degrees", ignoreCase = true)
                "TemperatureFormat" -> _temperatureFormatToggle.value = value.equals("Fahrenheit", ignoreCase = true)
            }
        }
    }

    /**
     * PERFORMANCE OPTIMIZATION: Reset all settings to defaults in one batch
     */
    fun resetToDefaults() {
        try {
            val defaultSettings = mapOf(
                "Hemisphere" to "North",
                "TimeFormat" to "12",
                "DateFormat" to "MM/dd/yyyy",
                "Email" to "",
                "WindDirection" to "Degrees",
                "TemperatureFormat" to "Fahrenheit",
                "Subscription" to "Free",
                "AddLocationViewed" to "false",
                "ListLocationsViewed" to "false",
                "EditLocationViewed" to "false",
                "WeatherViewed" to "false",
                "SettingsViewed" to "false",
                "SunLocationViewed" to "false",
                "SunCalculationViewed" to "false",
                "ExposureCalculationViewed" to "false",
                "SceneEvaluationViewed" to "false",
                "SubscriptionExpiration" to Clock.System.now().toString()
            )

            defaultSettings.forEach { (key, value) ->
                updateSettingByKey(key, value)
            }
        } catch (ex: Exception) {
            onSystemError("Error resetting settings: ${ex.message}")
        }
    }

    /**
     * Get all settings as a map for serialization/persistence
     */
    fun getAllSettings(): Map<String, String> {
        return mapOf(
            "Hemisphere" to (hemisphere.value.value.value.ifEmpty { "North" }),
            "TimeFormat" to (timeFormat.value.value.value.ifEmpty { "12" }),
            "DateFormat" to (dateFormat.value.value.value.ifEmpty { "MM/dd/yyyy" }),
            "Email" to (email.value.value.value.ifEmpty { "" }),
            "WindDirection" to (windDirection.value.value.value.ifEmpty { "Degrees" }),
            "TemperatureFormat" to (temperatureFormat.value.value.value.ifEmpty { "Fahrenheit" }),
            "Subscription" to (subscription.value.value.value.ifEmpty { "Free" }),
            "AddLocationViewed" to (addLocationViewed.value.value.value.ifEmpty { "false" }),
            "ListLocationsViewed" to (listLocationsViewed.value.value.value.ifEmpty { "false" }),
            "EditLocationViewed" to (editLocationViewed.value.value.value.ifEmpty { "false" }),
            "WeatherViewed" to (weatherViewed.value.value.value.ifEmpty { "false" }),
            "SettingsViewed" to (settingsViewed.value.value.value.ifEmpty { "false" }),
            "SunLocationViewed" to (sunLocationViewed.value.value.value.ifEmpty { "false" }),
            "SunCalculationViewed" to (sunCalculationViewed.value.value.value.ifEmpty { "false" }),
            "ExposureCalculationViewed" to (exposureCalculationViewed.value.value.value.ifEmpty { "false" }),
            "SceneEvaluationViewed" to (sceneEvaluationViewed.value.value.value.ifEmpty { "false" }),
            "SubscriptionExpiration" to (subscriptionExpiration.value.value.value.ifEmpty { Clock.System.now().toString() })
        )
    }

    /**
     * Helper method to update a single setting by key and value
     */
    fun updateSetting(key: String, value: String) {
        updateSettingByKey(key, value)
    }

    /**
     * Helper method to get a setting value by key
     */
    fun getSettingValue(key: String): String {
        return when (key) {
            "Hemisphere" -> hemisphere.value.value.value
            "TimeFormat" -> timeFormat.value.value.value
            "DateFormat" -> dateFormat.value.value.value
            "Email" -> email.value.value.value
            "WindDirection" -> windDirection.value.value.value
            "TemperatureFormat" -> temperatureFormat.value.value.value
            "Subscription" -> subscription.value.value.value
            "AddLocationViewed" -> addLocationViewed.value.value.value
            "ListLocationsViewed" -> listLocationsViewed.value.value.value
            "EditLocationViewed" -> editLocationViewed.value.value.value
            "WeatherViewed" -> weatherViewed.value.value.value
            "SettingsViewed" -> settingsViewed.value.value.value
            "SunLocationViewed" -> sunLocationViewed.value.value.value
            "SunCalculationViewed" -> sunCalculationViewed.value.value.value
            "ExposureCalculationViewed" -> exposureCalculationViewed.value.value.value
            "SceneEvaluationViewed" -> sceneEvaluationViewed.value.value.value
            "SubscriptionExpiration" -> subscriptionExpiration.value.value.value
            else -> ""
        }
    }
}