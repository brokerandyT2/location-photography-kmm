// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/OptimalWindowDisplayModel.kt
package com.x3squaredcircles.photography.models

import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class OptimalWindowDisplayModel(
    val windowType: String = "",
    val startTime: Instant,
    val endTime: Instant,
    val timeFormat: String = "HH:mm",
    val startTimeDisplay: String = "",
    val endTimeDisplay: String = "",
    val lightQuality: String = "",
    val optimalFor: String = "",
    val isCurrentlyActive: Boolean = false,
    val confidenceLevel: Double = 0.0
) {
    val isOptimalTime: Boolean
        get() = isCurrentlyActive || confidenceLevel >= 0.7

    val formattedTimeRange: String
        get() {
            val startLocal = startTime.toLocalDateTime(TimeZone.currentSystemDefault())
            val endLocal = endTime.toLocalDateTime(TimeZone.currentSystemDefault())
            return "${formatTime(startLocal.hour, startLocal.minute)} - ${formatTime(endLocal.hour, endLocal.minute)}"
        }

    val duration: Duration
        get() = endTime - startTime

    val durationDisplay: String
        get() {
            val hours = duration.inWholeHours
            val minutes = (duration.inWholeMinutes % 60)
            return "${hours}h ${minutes}m"
        }

    val confidenceDisplay: String
        get() = "${(confidenceLevel * 100).toInt()}% confidence"

    private fun formatTime(hour: Int, minute: Int): String {
        return when (timeFormat) {
            "HH:mm" -> "%02d:%02d".format(hour, minute)
            "h:mm tt" -> {
                val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                val amPm = if (hour < 12) "AM" else "PM"
                "%d:%02d %s".format(h, minute, amPm)
            }
            else -> "%02d:%02d".format(hour, minute)
        }
    }

    fun getFormattedStartTime(format: String): String {
        val startLocal = startTime.toLocalDateTime(TimeZone.currentSystemDefault())
        return formatTime(startLocal.hour, startLocal.minute)
    }

    fun getFormattedEndTime(format: String): String {
        val endLocal = endTime.toLocalDateTime(TimeZone.currentSystemDefault())
        return formatTime(endLocal.hour, endLocal.minute)
    }

    fun getFormattedTimeRange(format: String): String {
        val startLocal = startTime.toLocalDateTime(TimeZone.currentSystemDefault())
        val endLocal = endTime.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${formatTimeWithFormat(startLocal.hour, startLocal.minute, format)} - ${formatTimeWithFormat(endLocal.hour, endLocal.minute, format)}"
    }

    private fun formatTimeWithFormat(hour: Int, minute: Int, format: String): String {
        return when (format) {
            "HH:mm" -> "%02d:%02d".format(hour, minute)
            "h:mm tt" -> {
                val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                val amPm = if (hour < 12) "AM" else "PM"
                "%d:%02d %s".format(h, minute, amPm)
            }
            else -> "%02d:%02d".format(hour, minute)
        }
    }
}