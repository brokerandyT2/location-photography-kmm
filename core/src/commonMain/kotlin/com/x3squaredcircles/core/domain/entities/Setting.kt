// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Setting.kt
package com.x3squaredcircles.core.domain.entities
import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
/**

User setting entity
 */
class Setting private constructor(
    private var _id: Int = 0,
    private var _key: String,
    private var _value: String,
    val description: String = "",
    private var _timestamp: Instant = Clock.System.now()
) : Entity() {
    override val id: Int get() = _id
    val key: String get() = _key
    val value: String get() = _value
    val timestamp: Instant get() = _timestamp
    companion object {
        /**
         * Creates a new Setting instance
         */
        fun create(key: String, value: String, description: String = ""): Setting {
            require(key.isNotBlank()) { "Key cannot be empty" }
            return Setting(
                _key = key,
                _value = value,
                description = description
            )
        }

        /**
         * Creates a Setting instance from persistence (for repository use)
         */
        fun fromPersistence(
            id: Int,
            key: String,
            value: String,
            description: String = "",
            timestamp: Instant
        ): Setting {
            return Setting(
                _id = id,
                _key = key,
                _value = value,
                description = description,
                _timestamp = timestamp
            )
        }
    }
    /**

    Updates the setting value
     */
    fun updateValue(value: String) {
        _value = value
        _timestamp = Clock.System.now()
    }

    /**

    Gets the setting value as boolean
     */
    fun getBooleanValue(): Boolean {
        return _value.lowercase() in setOf("true", "1", "yes", "on")
    }

    /**

    Gets the setting value as integer with default fallback
     */
    fun getIntValue(defaultValue: Int = 0): Int {
        return _value.toIntOrNull() ?: defaultValue
    }

    /**

    Gets the setting value as double with default fallback
     */
    fun getDoubleValue(defaultValue: Double = 0.0): Double {
        return _value.toDoubleOrNull() ?: defaultValue
    }

    /**

    Internal method for setting ID (used by repositories)
     */
    internal fun setId(id: Int) {
        require(id > 0) { "Id must be greater than zero" }
        _id = id
    }
}