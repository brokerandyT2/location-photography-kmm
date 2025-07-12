// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/common/Entity.kt
package com.x3squaredcircles.core.domain.common

import kotlinx.serialization.Serializable

/**
 * Base class for all entities in the domain.
 * An entity is an object that is not defined by its attributes, but rather by its identity.
 */
@Serializable
abstract class Entity {
    /**
     * The unique identifier for this entity.
     */
    abstract val id: Int
    
    /**
     * Determines whether two entities are equal based on their ID.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        
        other as Entity
        return id == other.id
    }
    
    /**
     * Returns the hash code for this entity based on its ID.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }
    
    /**
     * Returns a string representation of this entity.
     */
    override fun toString(): String {
        return "${this::class.simpleName}(id=$id)"
    }
}