// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/common/AggregateRoot.kt
package com.x3squaredcircles.core.domain.common

import com.x3squaredcircles.core.domain.interfaces.IAggregateRoot
import com.x3squaredcircles.core.domain.interfaces.IDomainEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Base class for aggregate roots in Domain-Driven Design (DDD).
 * An aggregate root is the only member of its aggregate that outside objects are allowed to hold references to.
 */
@Serializable
abstract class AggregateRoot : Entity(), IAggregateRoot {
    
    @Transient
    private val _domainEvents = mutableListOf<IDomainEvent>()
    
    /**
     * Gets the collection of domain events associated with the current aggregate root.
     */
    override val domainEvents: List<IDomainEvent>
        get() = _domainEvents.toList()
    
    /**
     * Adds a domain event to the collection of events associated with the aggregate root.
     */
    override fun addDomainEvent(eventItem: IDomainEvent) {
        _domainEvents.add(eventItem)
    }
    
    /**
     * Removes a specified domain event from the collection of domain events.
     */
    override fun removeDomainEvent(eventItem: IDomainEvent) {
        _domainEvents.remove(eventItem)
    }
    
    /**
     * Clears all domain events associated with the current aggregate root.
     */
    override fun clearDomainEvents() {
        _domainEvents.clear()
    }
    
    /**
     * Helper method to raise a domain event.
     * This is a convenience method that adds the event to the domain events collection.
     */
    protected fun raiseDomainEvent(domainEvent: IDomainEvent) {
        addDomainEvent(domainEvent)
    }
}