// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/models/FixedValue.kt
package com.x3squaredcircles.photography.domain.models

/**
 * Defines which part of the exposure triangle should be calculated
 */
enum class FixedValue(val value: Int) {
    SHUTTER_SPEEDS(0),
    ISO(1),
    EMPTY(2),
    APERTURE(3);

    companion object {
        fun fromValue(value: Int): FixedValue {
            return values().find { it.value == value } ?: EMPTY
        }
    }
}