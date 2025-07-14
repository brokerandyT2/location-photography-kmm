// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/common/DomainEvent.kt
package com.x3squaredcircles.core.domain.common
import com.x3squaredcircles.core.domain.interfaces.IDomainEvent
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
/**

Base class for all domain events
 */
abstract class DomainEvent : IDomainEvent {
    override val dateOccurred: Instant
    /**

    Initializes a new instance of the DomainEvent class.
     */
    protected constructor() {
        dateOccurred = Clock.System.now()
    }
}