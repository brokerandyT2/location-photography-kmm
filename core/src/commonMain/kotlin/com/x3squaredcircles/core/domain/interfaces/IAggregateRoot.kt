// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/interfaces/IAggregateRoot.kt
package com.x3squaredcircles.core.domain.interfaces

/**
 * Marker interface for aggregate roots in Domain-Driven Design (DDD).
 * An aggregate root is the only member of its aggregate that outside objects are allowed to hold references to.
 */
interface IAggregateRoot {
    /**
     * Gets the collection of domain events associated with the current aggregate root.
     */
    val domainEvents: List<IDomainEvent>
    
    /**
     * Adds a domain event to the collection of events associated with the aggregate root.
     */
    fun addDomainEvent(eventItem: IDomainEvent)
    
    /**
     * Removes a specified domain event from the collection of domain events.
     */
    fun removeDomainEvent(eventItem: IDomainEvent)
    
    /**
     * Clears all domain events associated with the current aggregate root.
     */
    fun clearDomainEvents()
}