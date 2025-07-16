// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/events/ErrorOccurredEvent.kt
package com.x3squaredcircles.photography.events.errors

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Represents an event that occurs when an error is encountered, providing details about the error.
 */
class ErrorOccurredEvent(
    /**
     * The error message describing the nature of the error
     */
    val message: String,
    /**
     * The source or origin of the error
     */
    val source: String
) {
    /**
     * When this error occurred
     */
    val timestamp: Instant = Clock.System.now()

    init {
        require(message.isNotBlank()) { "Message cannot be null or blank" }
        require(source.isNotBlank()) { "Source cannot be null or blank" }
    }
}