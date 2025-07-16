// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/events/errors/DomainErrorEvent.kt
package com.x3squaredcircles.photography.events.errors

import com.benasher44.uuid.uuid4
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Base class for all domain-specific error events that can be published through MediatR
 */
abstract class DomainErrorEvent(
    /**
     * The source operation or handler that generated this error
     */
    val source: String
) {
    /**
     * Unique identifier for this error occurrence
     */
    val errorId: String = uuid4().toString()

    /**
     * When this error occurred
     */
    val timestamp: Instant = Clock.System.now()

    /**
     * Gets the resource key for localized error message
     */
    abstract fun getResourceKey(): String

    /**
     * Gets parameters for message formatting
     */
    open fun getParameters(): Map<String, Any> {
        return emptyMap()
    }

    /**
     * Gets the error severity level
     */
    open val severity: ErrorSeverity = ErrorSeverity.Error
}

/**
 * Defines the severity levels for error events
 */
enum class ErrorSeverity {
    Info,
    Warning,
    Error,
    Critical
}