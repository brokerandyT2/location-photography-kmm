// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/Setting.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable
import kotlinx.datetime.*

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
    val timestampDateTime: LocalDateTime
        get() = Instant.fromEpochMilliseconds(timestamp)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    
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
            timestamp = Clock.System.now().toEpochMilliseconds()
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
            require(key.isNotBlank()) { "Setting key cannot be blank" }
            
            val now = Clock.System.now().toEpochMilliseconds()
            return Setting(
                key = key.trim(),
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
        
        // Common photography setting keys
        object Keys {
            const val CAMERA_FLASH_ENABLED = "camera_flash_enabled"
            const val DEFAULT_ISO = "default_iso"
            const val PREFERRED_ASPECT_RATIO = "preferred_aspect_ratio"
            const val AUTO_SAVE_LOCATION = "auto_save_location"
            const val WEATHER_UPDATE_INTERVAL = "weather_update_interval"
            const val NOTIFICATION_ENABLED = "notification_enabled"
            const val UNIT_SYSTEM = "unit_system" // metric/imperial
            const val LANGUAGE_CODE = "language_code"
            const val THEME_MODE = "theme_mode" // light/dark/auto
            const val GPS_PRECISION = "gps_precision"
        }
    }
}