// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/valueobjects/ValueObject.kt
package com.x3squaredcircles.core.domain.valueobjects
/**

Base class for value objects
 */
abstract class ValueObject {
    /**

    Determines whether two ValueObject instances are equal.
     */
    protected fun equalOperator(left: ValueObject?, right: ValueObject?): Boolean {
        if ((left == null) xor (right == null)) {
            return false
        }
        return left == null || left == right
    }

    /**

    Determines whether two ValueObject instances are not equal.
     */
    protected fun notEqualOperator(left: ValueObject?, right: ValueObject?): Boolean {
        return !equalOperator(left, right)
    }

    /**

    Provides the components that define equality for the derived type.
     */
    protected abstract fun getEqualityComponents(): Sequence<Any?>

    /**

    Determines whether the specified object is equal to the current object.
     */
    override fun equals(other: Any?): Boolean {
        if (other == null || other::class != this::class) {
            return false
        }
        val otherValueObject = other as ValueObject
        return getEqualityComponents().zip(otherValueObject.getEqualityComponents())
            .all { (a, b) -> a == b } &&
                getEqualityComponents().count() == otherValueObject.getEqualityComponents().count()
    }

    /**

    Returns a hash code for the current object based on its equality components.
     */
    override fun hashCode(): Int {
        return getEqualityComponents()
            .map { it?.hashCode() ?: 0 }
            .fold(0) { acc, hash -> acc xor hash }
    }
}