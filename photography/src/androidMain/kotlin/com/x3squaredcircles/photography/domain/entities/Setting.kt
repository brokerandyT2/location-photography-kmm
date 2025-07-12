// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/Setting.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable
import kotlinx.datetime.toLocalDateTime
/**
 * Represents a photography-specific configuration setting for the application.
 * Settings use a key-value pattern and support type-safe value retrieval.
 */
@Serializable
data class Setting(
    override val id: Int = 0,
    val key: String,
    val value: String,
    val description: String = "",
    val timestamp: Long
) : Entity() {
    
    /**
     * Gets the timestamp as a LocalDateTime in the current system timezone.
     */
    val timestampDateTime: kotlinx.datetime.LocalDateTime
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(timestamp)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    /**
     * Gets the value as a Boolean. Returns false if conversion fails.
     */
    fun getBooleanValue(): Boolean {
        return when (value.lowercase()) {
            "true", "1", "yes", "on" -> true
            "false", "0", "no", "off" -> false
            else -> false
        }
    }
    
    /**
     * Gets the value as an Int. Returns 0 if conversion fails.
     */
    fun getIntValue(): Int {
        return value.toIntOrNull() ?: 0
    }
    
    /**
     * Gets the value as a Double. Returns 0.0 if conversion fails.
     */
    fun getDoubleValue(): Double {
        return value.toDoubleOrNull() ?: 0.0
    }
    
    /**
     * Gets the value as a Long. Returns 0L if conversion fails.
     */
    fun getLongValue(): Long {
        return value.toLongOrNull() ?: 0L
    }
    
    /**
     * Gets the value as a String (no conversion needed).
     */
    fun getStringValue(): String = value
    
    /**
     * Checks if the setting value represents a "true" boolean value.
     */
    val isTrue: Boolean
        get() = getBooleanValue()
    
    /**
     * Checks if the setting value represents a "false" boolean value.
     */
    val isFalse: Boolean
        get() = !getBooleanValue()
    
    /**
     * Checks if the setting value is empty or blank.
     */
    val isEmpty: Boolean
        get() = value.isBlank()
    
    /**
     * Creates a copy of this setting with a new value and updated timestamp.
     */
    fun updateValue(newValue: String): Setting {
        return copy(
            value = newValue,
            timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )
    }
    
    /**
     * Creates a copy of this setting with a boolean value.
     */
    fun updateValue(newValue: Boolean): Setting {
        return updateValue(newValue.toString())
    }
    
    /**
     * Creates a copy of this setting with an integer value.
     */
    fun updateValue(newValue: Int): Setting {
        return updateValue(newValue.toString())
    }
    
    /**
     * Creates a copy of this setting with a double value.
     */
    fun updateValue(newValue: Double): Setting {
        return updateValue(newValue.toString())
    }
    
    companion object {
        /**
         * Creates a new Setting with the current timestamp.
         */
        fun create(
            key: String,
            value: String,
            description: String = ""
        ): Setting {
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            return Setting(
                key = key,
                value = value,
                description = description,
                timestamp = now
            )
        }
        
        /**
         * Creates a new Setting with a boolean value.
         */
        fun create(key: String, value: Boolean, description: String = ""): Setting {
            return create(key, value.toString(), description)
        }
        
        /**
         * Creates a new Setting with an integer value.
         */
        fun create(key: String, value: Int, description: String = ""): Setting {
            return create(key, value.toString(), description)
        }
        
        /**
         * Creates a new Setting with a double value.
         */
        fun create(key: String, value: Double, description: String = ""): Setting {
            return create(key, value.toString(), description)
        }
        
        // Photography-specific setting keys
        object Keys {
            const val HEMISPHERE = "Hemisphere"
            const val TIME_FORMAT = "TimeFormat"
            const val DATE_FORMAT = "DateFormat"
            const val EMAIL = "Email"
            const val WIND_DIRECTION = "WindDirection"
            const val TEMPERATURE_FORMAT = "TemperatureFormat"
            const val SUBSCRIPTION = "Subscription"
            const val ADD_LOCATION_VIEWED = "AddLocationViewed"
            const val LIST_LOCATIONS_VIEWED = "ListLocationsViewed"
            const val EDIT_LOCATION_VIEWED = "EditLocationViewed"
            const val WEATHER_VIEWED = "WeatherViewed"
            const val SETTINGS_VIEWED = "SettingsViewed"
            const val SUN_LOCATION_VIEWED = "SunLocationViewed"
            const val SUN_CALCULATION_VIEWED = "SunCalculationViewed"
            const val EXPOSURE_CALCULATION_VIEWED = "ExposureCalculationViewed"
            const val SCENE_EVALUATION_VIEWED = "SceneEvaluationViewed"
            const val SUBSCRIPTION_EXPIRATION = "SubscriptionExpiration"
        }
    }
}