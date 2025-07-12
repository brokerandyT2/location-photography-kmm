// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/interfaces/IDomainEvent.kt
package com.x3squaredcircles.core.domain.interfaces

import kotlinx.serialization.Serializable

/**
 * Marker interface for domain events in Domain-Driven Design (DDD).
 * Domain events represent something that happened in the domain that domain experts care about.
 */

interface IDomainEvent {
    /**
     * The timestamp when this domain event occurred (in milliseconds since epoch).
     */
    val dateOccurred: Long
}