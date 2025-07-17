// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/events/errors/SettingErrorEvent.kt
package com.x3squaredcircles.photography.events.errors

/**
 * Defines the types of setting errors that can occur
 */
enum class SettingErrorType {
    DuplicateKey,
    KeyNotFound,
    InvalidValue,
    ReadOnlySetting,
    DatabaseError
}

/**
 * Event representing setting errors that occurred during processing
 */
class SettingErrorEvent(
    /**
     * The setting key that caused the error
     */
    val settingKey: String,
    /**
     * The specific type of error that occurred
     */
    val errorType: SettingErrorType,
    /**
     * Additional context about the error
     */
    val additionalContext: String? = null
) : DomainErrorEvent("SettingCommandHandler") {

    override fun getResourceKey(): String {
        return when (errorType) {
            SettingErrorType.DuplicateKey -> "Setting_Error_DuplicateKey"
            SettingErrorType.KeyNotFound -> "Setting_Error_KeyNotFound"
            SettingErrorType.InvalidValue -> "Setting_Error_InvalidValue"
            SettingErrorType.ReadOnlySetting -> "Setting_Error_ReadOnlySetting"
            SettingErrorType.DatabaseError -> "Setting_Error_DatabaseError"
        }
    }

    override fun getParameters(): Map<String, Any> {
        val parameters = mutableMapOf<String, Any>(
            "SettingKey" to settingKey
        )

        additionalContext?.let {
            if (it.isNotBlank()) {
                parameters["AdditionalContext"] = it
            }
        }

        return parameters
    }
}