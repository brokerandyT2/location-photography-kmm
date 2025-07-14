// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/common/Entity.kt
package com.x3squaredcircles.core.domain.common
import com.x3squaredcircles.core.domain.interfaces.IEntity
/**

Base class for all domain entities
 */
abstract class Entity : IEntity {
    private var requestedHashCode: Int? = null
    abstract override val id: Int
    /**

    Determines whether the entity is considered transient.
     */
    fun isTransient(): Boolean {
        return id == 0
    }

    /**

    Determines whether the specified object is equal to the current entity.
     */
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Entity) {
            return false
        }
        if (this === other) {
            return true
        }
        if (this::class != other::class) {
            return false
        }
        if (isTransient() || other.isTransient()) {
            return false
        }
        return id == other.id
    }

    /**

    Serves as the default hash function for the object, providing a hash code based on the object's identifier.
     */
    override fun hashCode(): Int {
        if (!isTransient()) {
            if (requestedHashCode == null) {
                requestedHashCode = id.hashCode() xor 31
            }
            return requestedHashCode!!
        } else {
            return super.hashCode()
        }
    }
}